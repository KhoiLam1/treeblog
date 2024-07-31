package com.example.tree.tips

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tree.R
import com.example.tree.tips.models.Tip
import com.example.tree.tips.view_models.TipsViewModel

@Composable
fun TipMainScreen(
    viewModel: TipsViewModel,
    onProductTipClick: (Tip) -> Unit,
    onFabClick: () -> Unit
) {
    val topTipList by viewModel.topTipList.observeAsState(emptyList())
    val tipList by viewModel.tipList.observeAsState(emptyList())
    val sortDirection by viewModel.sortDirection.observeAsState(TipsViewModel.SORT_BY_NEWEST)
    val user by viewModel.user.observeAsState()

    Scaffold(
        floatingActionButton = {
            user?.let {
                if (it.role == "writer") {
                    ExtendedFloatingActionButton(
                        text = { Text("New Tip") },
                        icon = { Icon(painterResource(id = R.drawable.edit_24px), contentDescription = "New Tip") },
                        onClick = onFabClick,
                        containerColor = Color(0xFF5A8659),
                        contentColor = Color.White
                    )
                }
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                Text(
                    text = "Recommend",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(topTipList) { tip ->
                        CarouselItem(tip = tip, onClick = onProductTipClick)
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tips Feed",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                SortChip(
                    selectedSortDirection = sortDirection,
                    onSortDirectionSelected = { direction ->
                        viewModel.sortDirection.value = direction
                        viewModel.queryAllTips(direction)
                    }
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tipList) { tip ->
                        TipItem(tip = tip, onClick = onProductTipClick)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CarouselItem(tip: Tip, onClick: (Tip) -> Unit) {
    Card(
        modifier = Modifier
            .size(200.dp)
            .padding(8.dp),
        onClick = { onClick(tip) }
    ) {
        Column {
            GlideImage(
                model = tip.imageList.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TipItem(tip: Tip, onClick: (Tip) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(tip) }
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            GlideImage(
                model = tip.imageList.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "${tip.vote_count} votes",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SortChip(
    selectedSortDirection: Int,
    onSortDirectionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        R.id.sort_by_newest to Pair("Newest first", R.drawable.newest_24px),
        R.id.sort_by_vote to Pair("Most voted", R.drawable.volunteer_activism_24px),
        R.id.sort_by_oldest to Pair("Oldest first", R.drawable.oldest_24px)
    )

    val selectedOption = options.firstOrNull { it.first == selectedSortDirection }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = selectedOption?.second?.second ?: R.drawable.newest_24px),
                    contentDescription = null
                )
            },
            label = { Text(selectedOption?.second?.first ?: "Select") },
            modifier = Modifier.padding(4.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (id, labelIconPair) ->
                val (label, iconRes) = labelIconPair
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSortDirectionSelected(id)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TipMainScreenFragmentContainer() {
    val viewModel: TipsViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.queryAllTips(TipsViewModel.SORT_BY_NEWEST)
        viewModel.queryTopTips()
    }

    TipMainScreen(
        viewModel = viewModel,
        onProductTipClick = { tip ->
            val intent = Intent(context, TipDetailActivity::class.java).apply {
                putExtra("tipData", tip)
            }
            context.startActivity(intent)
        },
        onFabClick = {
            val intent = Intent(context, WriteTipActivity::class.java)
            context.startActivity(intent)
        }
    )
}