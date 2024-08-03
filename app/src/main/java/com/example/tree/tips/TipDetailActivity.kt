package com.example.tree.tips

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.tree.MainActivity
import com.example.tree.R
import com.example.tree.tips.models.Author
import com.example.tree.tips.models.Tip
import com.example.tree.tips.view_models.CommentViewModel
import com.example.tree.tips.view_models.TipsViewModel
import com.example.tree.users.activities.CustomGreen
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class TipDetailActivity : ComponentActivity() {
    private val tipsViewModel: TipsViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tip = intent.getParcelableExtra<Tip>("tipData")!!

        tipsViewModel.authorLiveData.observe(this) { author ->
            if (author != null) {
                setContent {
                    TipDetailScreen(
                        tip = tip,
                        author = author,
                        tipsViewModel = tipsViewModel,
                        commentViewModel = commentViewModel,
                        onBackClick = { navigateBack() }
                    )
                }
            } else {
                // Handle error case, maybe show a message
            }
        }

        tipsViewModel.getAuthor(tip.userId)
    }

    private fun navigateBack() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipDetailScreen(
    tip: Tip,
    author: Author,
    tipsViewModel: TipsViewModel,
    commentViewModel: CommentViewModel,
    onBackClick: () -> Unit
) {
    val comments by commentViewModel.commentList.observeAsState(emptyList())
    val isUpvoted by tipsViewModel.getIsUpvoted(tip.id).observeAsState()

    LaunchedEffect(tip) {
        commentViewModel.queryComments(tip)
    }

    var newComment by remember { mutableStateOf(TextFieldValue("")) }
    var isPlaying by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val textToSpeechHelper = remember { TextToSpeechHelper(context) }

    DisposableEffect(Unit) {
        onDispose { textToSpeechHelper.shutdown() }
    }

    var upvoted by remember { mutableStateOf(isUpvoted == true) }
    var downvoted by remember { mutableStateOf(isUpvoted == false) }

    LaunchedEffect(isUpvoted) {
        upvoted = isUpvoted == true
        downvoted = isUpvoted == false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tip Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Display Image from imageList
                Image(
                    painter = rememberAsyncImagePainter(tip.imageList[0]),
                    contentDescription = "Tip Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .padding(bottom = 16.dp)
                )

                // Title and Speaker Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        if (isPlaying) {
                            textToSpeechHelper.stop()
                        } else {
                            textToSpeechHelper.speak(tip.title, "Author", tip.content)
                        }
                        isPlaying = !isPlaying
                    }) {
                        Icon(
                            painter = painterResource(id = if (isPlaying) R.drawable.ic_speaker_playing else R.drawable.ic_speaker_idle),
                            contentDescription = "Speaker"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Voting Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            if (upvoted) {
                                upvoted = false
                                tipsViewModel.unVoteTip(tip, true)
                            } else if (!downvoted) {
                                upvoted = true
                                tipsViewModel.castVote(tip, true)
                            }
                            Log.d("TipDetailScreen", "Upvote button clicked: $upvoted")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            bottomStart = 24.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        border = BorderStroke(1.dp, Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (upvoted) CustomGreen else Color.White,
                            contentColor = if (upvoted) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upvote Arrow",
                            tint = if (upvoted) Color.White else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (upvoted) "Unvote" else "Upvote", color = if (upvoted) Color.White else Color.Black)
                    }
                    OutlinedButton(
                        onClick = {
                            if (downvoted) {
                                downvoted = false
                                tipsViewModel.unVoteTip(tip, false)
                            } else if (!upvoted) {
                                downvoted = true
                                tipsViewModel.castVote(tip, false)
                            }
                            Log.d("TipDetailScreen", "Downvote button clicked: $downvoted")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 24.dp,
                            bottomEnd = 24.dp
                        ),
                        border = BorderStroke(1.dp, Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (downvoted) Color.Red else Color.White,
                            contentColor = if (downvoted) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Downvote Arrow",
                            tint = if (downvoted) Color.White else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (downvoted) "Unvote" else "Downvote", color = if (downvoted) Color.White else Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Author Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Log.d("TipDetailScreen", "Author Avatar URL: ${author.writerAvatar}")
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(author.writerAvatar)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.img_placeholder), // Use a placeholder image
                            error = painterResource(R.drawable.avatar_default_2) // Use an error image
                        ),
                        contentDescription = "Author Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                            .clip(CircleShape) // Make the image circular
                    )
                    Column {
                        Text(text = author.fullName, style = MaterialTheme.typography.bodyLarge)
                        Text(text = author.writerName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    val sdf = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH)
                    val formattedDate = tip.createdAt?.let { sdf.format(it) + "  |  " } + tip.vote_count.toString() + " votes"
                    Text(text = formattedDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Comments Section
                Text(text = "Comments", style = MaterialTheme.typography.titleSmall, color = Color.Gray)

                // Add Comment Input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                            .padding(8.dp),
                        decorationBox = { innerTextField ->
                            if (newComment.text.isEmpty()) {
                                Text("Share your thought...", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                    IconButton(onClick = {
                        commentViewModel.castComment(tip, newComment.text)
                        newComment = TextFieldValue("")
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send Comment")
                    }
                }
            }

            // Display Comments
            items(comments) { comment ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = comment.fullName, style = MaterialTheme.typography.bodyLarge)
                    Text(text = comment.content, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TipDetailScreenPreview() {
//    val sampleTip = Tip(
//        id = "1",
//        title = "Sample Tip",
//        content = "This is a sample tip content.",
//        imageList = listOf("https://via.placeholder.com/200").toMutableList(),
//        createdAt = null,
//        updatedAt = null,
//        userId = "1",
//        vote_count = 100
//    )
//    val sampleAuthor = Author(
//        userId = "1",
//        fullName = "John",
//        writerName = "John Nguyen",
//        avatar = "https://via.placeholder.com/40"
//    )
//    TipDetailScreen(tip = sampleTip, author = sampleAuthor, tipsViewModel = TipsViewModel(), commentViewModel = CommentViewModel())
//}