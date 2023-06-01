package com.example.storyapp.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.getOrAwaitValue
import com.example.storyapp.model_responses.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private var dummyStory = DataDummy.dummyStory()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Quote Should Not Null and Return Data`() = mainDispatcherRules.runBlockingTest {
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory.listStory)
        val expectedResponse = MutableLiveData<PagingData<Story>>()

        expectedResponse.value = data
        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        val storiesViewModel = StoriesViewModel(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.listStory, differ.snapshot())
        assertEquals(dummyStory.listStory.size, differ.snapshot().size)
        assertEquals(dummyStory.listStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = mainDispatcherRules.runBlockingTest {
        val data = PagingData.from(emptyList<Story>())
        val expectedResponse = MutableLiveData<PagingData<Story>>()

        expectedResponse.value = data
        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        val storiesViewModel = StoriesViewModel(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }
}


class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

@ExperimentalCoroutinesApi
class MainDispatcherRule(private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()):
    TestWatcher(),
    TestCoroutineScope by createTestCoroutineScope(TestCoroutineDispatcher() + TestCoroutineExceptionHandler() + dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}


