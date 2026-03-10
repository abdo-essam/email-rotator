package com.ae.emailrotator.presentation.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.tool.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolsState(
    val tools: List<Tool> = emptyList(),
    val isLoading: Boolean = true,
    val snackbar: String? = null
)

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val getTools: GetToolsUseCase,
    private val addTool: AddToolUseCase,
    private val updateTool: UpdateToolUseCase,
    private val deleteTool: DeleteToolUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ToolsState())
    val state: StateFlow<ToolsState> = _state.asStateFlow()

    init {
        observeTools()
    }

    private fun observeTools() {
        viewModelScope.launch {
            getTools().collect { list ->
                _state.update { it.copy(tools = list, isLoading = false) }
            }
        }
    }

    fun saveTool(name: String, id: Long = 0L) {
        viewModelScope.launch {
            val tool = Tool(id = id, name = name)
            if (id == 0L) addTool(tool) else updateTool(tool)
            _state.update { it.copy(snackbar = if (id == 0L) "Tool added." else "Tool updated.") }
        }
    }

    fun deleteTool(id: Long) {
        viewModelScope.launch {
            deleteTool.invoke(id)
            _state.update { it.copy(snackbar = "Tool deleted.") }
        }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}
