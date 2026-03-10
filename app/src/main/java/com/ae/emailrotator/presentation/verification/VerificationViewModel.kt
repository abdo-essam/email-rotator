package com.ae.emailrotator.presentation.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.email.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerificationState(
    val emails: List<Email> = emptyList(),
    val isLoading: Boolean = true,
    val snackbar: String? = null
)

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationState())
    val state: StateFlow<VerificationState> = _state.asStateFlow()

    init {
        observeVerificationEmails()
    }

    private fun observeVerificationEmails() {
        viewModelScope.launch {
            getEmails.byStatus(EmailStatus.NEEDS_VERIFICATION)
                .collect { list ->
                    _state.update { it.copy(emails = list, isLoading = false) }
                }
        }
    }

    fun verifyEmail(emailId: Long, toolId: Long) {
        viewModelScope.launch {
            verifyEmailUseCase(emailId, toolId)
            _state.update { it.copy(snackbar = "Email verified successfully.") }
        }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}
