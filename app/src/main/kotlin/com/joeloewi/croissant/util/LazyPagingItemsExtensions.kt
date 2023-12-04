package com.joeloewi.croissant.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

fun LazyPagingItems<*>.isEmpty(): Boolean = with(this.loadState) {
    refresh is LoadState.NotLoading && append.endOfPaginationReached
} && itemCount == 0