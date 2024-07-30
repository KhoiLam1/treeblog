package com.example.tree.tips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tree.R
import com.example.tree.tips.models.Tip
import com.example.tree.tips.view_models.CommentViewModel
import com.example.tree.tips.view_models.TipsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class TipDetailActivity : ComponentActivity() {
    private val tipsViewModel: TipsViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipDetailScreen(tip = intent.getParcelableExtra("tipData")!!, tipsViewModel, commentViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipDetailScreen(tip: Tip, tipsViewModel: TipsViewModel, commentViewModel: CommentViewModel) {
    val comments by commentViewModel.commentList.observeAsState(emptyList())

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tip Details") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(painter = painterResource(id = R.drawable.arrow_back_24px), contentDescription = "Back")
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

                // Tip Image
                Image(
                    painter = rememberAsyncImagePainter(tip.imageList[0]),
                    contentDescription = "Tip image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { tipsViewModel.castVote(tip, true) }) {
                        Text(text = "Upvote")
                    }
                    Button(onClick = { tipsViewModel.castVote(tip, false) }) {
                        Text(text = "Downvote")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Author Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(tip.imageList[0]),
                        contentDescription = "Author Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )
                    Column {
                        Text(text = "", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                        Icon(painter = painterResource(id = R.drawable.send_48px), contentDescription = "Send Comment")
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

@Preview(showBackground = true)
@Composable
fun TipDetailScreenPreview() {
    val sampleTip = Tip(
        id = "1",
        title = "Sample Tip",
        content = "This is a sample tip content.",
        imageList = listOf("https://via.placeholder.com/150").toMutableList(),
        createdAt = null,
        updatedAt = null,
        shortDescription = "Sample short description",
        userId = "1",
        vote_count = 100
    )
    TipDetailScreen(tip = sampleTip, tipsViewModel = TipsViewModel(), commentViewModel = CommentViewModel())
}
