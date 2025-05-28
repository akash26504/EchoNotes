package com.example.videosummarise.ui.selection

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videosummarise.databinding.FragmentVideoSelectionBinding
import com.example.videosummarise.ui.selection.adapter.VideoAdapter
import com.example.videosummarise.ui.selection.model.Video

class VideoSelectionFragment : Fragment() {

    private var _binding: FragmentVideoSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoAdapter: VideoAdapter
    private var selectedVideo: Video? = null
    private val videos = mutableListOf<Video>()

    // Permission launcher for reading external storage
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadVideosFromDevice()
        } else {
            Toast.makeText(requireContext(), "Permission denied. Cannot access videos.", Toast.LENGTH_SHORT).show()
        }
    }

    // Video picker launcher for browsing videos
    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val video = createVideoFromUri(it)
            selectVideo(video)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        checkPermissionAndLoadVideos()
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter { video ->
            selectVideo(video)
        }

        binding.videosRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = videoAdapter
        }
    }

    private fun setupClickListeners() {
        binding.browseButton.setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }

        binding.processButton.setOnClickListener {
            selectedVideo?.let { video ->
                println("Processing button clicked for video: ${video.name}, URI: ${video.uri}")
                navigateToProcessing(video.uri)
            } ?: run {
                println("No video selected when process button clicked")
                Toast.makeText(requireContext(), "Please select a video first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndLoadVideos() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                loadVideosFromDevice()
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun loadVideosFromDevice() {
        val videoList = mutableListOf<Video>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )

        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
        val selectionArgs = arrayOf("1000") // Only videos longer than 1 second
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val duration = it.getLong(durationColumn)
                val size = it.getLong(sizeColumn)
                val data = it.getString(dataColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val video = Video(
                    id = id,
                    name = name,
                    uri = contentUri,
                    duration = duration,
                    size = size,
                    path = data
                )

                videoList.add(video)
            }
        }

        videos.clear()
        videos.addAll(videoList)
        videoAdapter.submitList(videos.toList())

        // Debug: Show how many videos were loaded
        Toast.makeText(requireContext(), "Loaded ${videoList.size} videos", Toast.LENGTH_SHORT).show()

        updateUI()
    }

    private fun createVideoFromUri(uri: Uri): Video {
        val cursor = requireContext().contentResolver.query(
            uri,
            arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
            ),
            null,
            null,
            null
        )

        var name = "Selected Video"
        var duration = 0L
        var size = 0L

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val durationIndex = it.getColumnIndex(MediaStore.Video.Media.DURATION)
                val sizeIndex = it.getColumnIndex(MediaStore.Video.Media.SIZE)

                if (nameIndex >= 0) name = it.getString(nameIndex) ?: "Selected Video"
                if (durationIndex >= 0) duration = it.getLong(durationIndex)
                if (sizeIndex >= 0) size = it.getLong(sizeIndex)
            }
        }

        return Video(
            id = System.currentTimeMillis(),
            name = name,
            uri = uri,
            duration = duration,
            size = size,
            path = uri.toString()
        )
    }

    private fun selectVideo(video: Video) {
        selectedVideo = video
        videoAdapter.setSelectedVideo(video)
        updateUI()

        // Show feedback to user
        Toast.makeText(requireContext(), "Selected: ${video.name}", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val hasVideos = videos.isNotEmpty()
        val hasSelection = selectedVideo != null

        binding.videosRecyclerView.visibility = if (hasVideos) View.VISIBLE else View.GONE
        binding.noVideoText.visibility = if (hasVideos) View.GONE else View.VISIBLE
        binding.processButton.isEnabled = hasSelection

        // Update no video text based on state
        binding.noVideoText.text = if (hasVideos) {
            "Tap a video to select it"
        } else {
            "No videos found. Try browsing for videos."
        }
    }

    private fun navigateToProcessing(videoUri: Uri) {
        try {
            println("Navigating to processing with URI: $videoUri")
            val action = VideoSelectionFragmentDirections
                .actionVideoSelectionFragmentToProcessingFragment(videoUri.toString())
            findNavController().navigate(action)
            println("Navigation successful")
        } catch (e: Exception) {
            println("Navigation error: ${e.message}")
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error processing video: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}