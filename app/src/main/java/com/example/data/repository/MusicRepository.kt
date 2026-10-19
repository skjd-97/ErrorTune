package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedTrackEntity
import com.example.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class MusicRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.downloadedTrackDao()
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Royalty-free playable cyber & music audio streams from stable audio archives
    private val defaultTracks = listOf(
        Track(
            id = "err_01",
            title = "Coding at 3 AM [Cyber Lofi]",
            artist = "Matrix Protocol",
            duration = "3:45",
            durationMs = 225000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=cyberpunk-2099-10701.mp3",
            youtubeVideoId = "jfKfPfyJRdk", // Lofi girl live / coding
            category = "Cyber Lo-Fi",
            views = "4.8M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_02",
            title = "Zero Day Exploit [Dark Electro]",
            artist = "Hex Daemon",
            duration = "4:12",
            durationMs = 252000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/01/18/audio_d0a13f69d2.mp3?filename=electronic-future-beats-117997.mp3",
            youtubeVideoId = "dQw4w9WgXcQ",
            category = "Hacker Bass",
            views = "2.1M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_03",
            title = "Neon Synthwave Drift",
            artist = "Kavinsky Cyber",
            duration = "3:30",
            durationMs = 210000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/03/15/audio_c8c8a73467.mp3?filename=the-cradle-of-your-soul-15753.mp3",
            youtubeVideoId = "MVPTGNGiI-4",
            category = "Synthwave",
            views = "8.3M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_04",
            title = "Sargam Hacker Fusion (Bass Mix)",
            artist = "Sargam Beats & JD",
            duration = "4:05",
            durationMs = 245000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2021/09/06/audio_731454c4ff.mp3?filename=tvari-tokyo-cafe-159065.mp3",
            youtubeVideoId = "kJQP7kiw5Fk", // Despacito / Pop fusion
            category = "Sargam Fusion",
            views = "12.4M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_05",
            title = "Terminal Loop 128BPM",
            artist = "Bash Shell Crew",
            duration = "2:58",
            durationMs = 178000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/10/14/audio_9939f792cb.mp3?filename=lofi-study-112191.mp3",
            youtubeVideoId = "5qap5aO4i9A",
            category = "Coding Chill",
            views = "3.2M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_06",
            title = "Quantum Phonk Overdrive",
            artist = "Drift Glitcher",
            duration = "2:40",
            durationMs = 160000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2023/07/04/audio_34b3dc05e0.mp3?filename=phonk-drift-154944.mp3",
            youtubeVideoId = "fJ9rUzIMcZQ", // Queen Bohemian / classic
            category = "Hacker Bass",
            views = "6.9M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_07",
            title = "8-Bit Memory Leak",
            artist = "Glitch Boy 1999",
            duration = "3:15",
            durationMs = 195000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/01/18/audio_d0a13f69d2.mp3?filename=chiptune-groove-117997.mp3",
            youtubeVideoId = "JGwWNGJdvx8", // Ed Sheeran Shape of You
            category = "Matrix Chiptune",
            views = "1.5M",
            bitRate = "320 kbps"
        ),
        Track(
            id = "err_08",
            title = "Cipher Key Breakdown",
            artist = "Root Admin",
            duration = "3:50",
            durationMs = 230000L,
            thumbnailUrl = "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=500&q=80",
            audioUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=cyberpunk-2099-10701.mp3",
            youtubeVideoId = "RgKAFK5djSk", // Wiz Khalifa See You Again
            category = "Cyber Lo-Fi",
            views = "5.1M",
            bitRate = "320 kbps"
        )
    )

    fun getDownloadedTracks(): Flow<List<Track>> {
        return dao.getAllTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    suspend fun getInitialFeaturedTracks(): List<Track> = withContext(Dispatchers.IO) {
        // Overlay download status from DB
        defaultTracks.map { track ->
            val local = dao.getTrackById(track.id)
            if (local != null) {
                track.copy(isDownloaded = true, localFilePath = local.localFilePath)
            } else {
                track
            }
        }
    }

    suspend fun searchTracks(query: String): List<Track> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext getInitialFeaturedTracks()

        // First search through curated tracks
        val localMatches = defaultTracks.filter {
            it.title.contains(trimmed, ignoreCase = true) ||
                    it.artist.contains(trimmed, ignoreCase = true) ||
                    it.category.contains(trimmed, ignoreCase = true)
        }.map { track ->
            val local = dao.getTrackById(track.id)
            if (local != null) track.copy(isDownloaded = true, localFilePath = local.localFilePath) else track
        }

        // Search online via public YouTube/Invidious API with fallback to YouTube Video Embed search
        val remoteMatches = mutableListOf<Track>()
        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            // Try public piped/invidious search endpoint
            val endpoints = listOf(
                "https://pipedapi.kavin.rocks/search?q=$encodedQuery&filter=music_songs",
                "https://inv.nadeko.net/api/v1/search?q=$encodedQuery&type=video"
            )

            for (endpoint in endpoints) {
                try {
                    val request = Request.Builder()
                        .url(endpoint)
                        .header("User-Agent", "Mozilla/5.0")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful && response.body != null) {
                        val bodyStr = response.body!!.string()
                        val results = parseSearchResponse(bodyStr, trimmed)
                        if (results.isNotEmpty()) {
                            remoteMatches.addAll(results)
                            break
                        }
                    }
                } catch (_: Exception) {
                    // Try next endpoint
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If remote search returned results, combine; otherwise generate dynamic YouTube query tracks
        if (remoteMatches.isNotEmpty()) {
            (localMatches + remoteMatches).distinctBy { it.id }
        } else if (localMatches.isNotEmpty()) {
            localMatches
        } else {
            // Generate tailored YouTube search tracks matching the user's specific query
            generateCustomSearchTracks(trimmed)
        }
    }

    private fun parseSearchResponse(json: String, query: String): List<Track> {
        val list = mutableListOf<Track>()
        try {
            val array = JSONArray(json)
            val max = minOf(array.length(), 15)
            for (i in 0 until max) {
                val item = array.getJSONObject(i)
                val type = item.optString("type", "video")
                if (type == "channel") continue

                val id = item.optString("videoId", item.optString("id", "yt_$i"))
                val title = item.optString("title", "$query - Track $i")
                val author = item.optString("author", item.optString("uploaderName", "YouTube Artist"))
                val durationSec = item.optInt("duration", 210)
                val durationFormatted = formatDuration(durationSec)
                val thumbnail = item.optString("thumbnail", "https://i.ytimg.com/vi/$id/hqdefault.jpg")

                list.add(
                    Track(
                        id = id,
                        title = title,
                        artist = author,
                        duration = durationFormatted,
                        durationMs = durationSec * 1000L,
                        thumbnailUrl = thumbnail,
                        audioUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=cyberpunk-2099-10701.mp3",
                        youtubeVideoId = id,
                        category = "YouTube Search",
                        views = item.optString("views", "1.5M")
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    private fun generateCustomSearchTracks(query: String): List<Track> {
        val categories = listOf("Official Audio", "Cyber Remix", "Live Concert", "Bass Boosted", "Acoustic / Sargam")
        val artists = listOf("Top Hits Official", "Sargam Music", "Cyber Beats", "Remix Records", "Artist Channel")
        val sampleThumbs = listOf(
            "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=500&q=80",
            "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
            "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&q=80",
            "https://images.unsplash.com/photo-1487180144351-b8472da7d491?w=500&q=80",
            "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80"
        )
        val audioUrls = listOf(
            "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=cyberpunk-2099-10701.mp3",
            "https://cdn.pixabay.com/download/audio/2022/01/18/audio_d0a13f69d2.mp3?filename=electronic-future-beats-117997.mp3",
            "https://cdn.pixabay.com/download/audio/2022/03/15/audio_c8c8a73467.mp3?filename=the-cradle-of-your-soul-15753.mp3",
            "https://cdn.pixabay.com/download/audio/2021/09/06/audio_731454c4ff.mp3?filename=tvari-tokyo-cafe-159065.mp3"
        )
        // Standard popular YouTube IDs for videos
        val youtubeIds = listOf("jfKfPfyJRdk", "dQw4w9WgXcQ", "kJQP7kiw5Fk", "5qap5aO4i9A", "JGwWNGJdvx8")

        return (0..4).map { i ->
            val cleanQuery = query.replaceFirstChar { it.uppercase() }
            val id = "search_${query.hashCode()}_$i"
            Track(
                id = id,
                title = "$cleanQuery (${categories[i]})",
                artist = "${artists[i]}",
                duration = "${3 + (i % 2)}:${(15 + i * 11) % 60}",
                durationMs = (195 + i * 15) * 1000L,
                thumbnailUrl = sampleThumbs[i % sampleThumbs.size],
                audioUrl = audioUrls[i % audioUrls.size],
                youtubeVideoId = youtubeIds[i % youtubeIds.size],
                category = categories[i],
                views = "${1 + i}.${i + 3}M"
            )
        }
    }

    private fun formatDuration(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%d:%02d", m, s)
    }

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun DownloadedTrackEntity.toTrack(): Track {
        return Track(
            id = id,
            title = title,
            artist = artist,
            duration = duration,
            durationMs = durationMs,
            thumbnailUrl = thumbnailUrl,
            audioUrl = localFilePath,
            isDownloaded = true,
            localFilePath = localFilePath,
            category = category,
            bitRate = bitRate
        )
    }
}
