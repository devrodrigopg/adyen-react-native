/*
 * Copyright (c) 2023 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by ozgur on 3/1/2023.
 */

package com.adyenreactnativesdk.component.instant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adyen.checkout.components.core.action.Action
import com.adyen.checkout.instant.InstantPaymentComponent
import com.adyen.checkout.instant.InstantPaymentConfiguration
import com.adyen.checkout.redirect.RedirectComponent
import com.adyenreactnativesdk.databinding.FragmentInstantBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class InstantFragment(private val configuration: InstantPaymentConfiguration) : BottomSheetDialogFragment() {

    private var _binding: FragmentInstantBinding? = null
    private val binding: FragmentInstantBinding
        get() = requireNotNull(_binding)

    private var instantPaymentComponent: InstantPaymentComponent? = null

    private val instantViewModel: InstantViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Insert return url in extras, so we can access it in the ViewModel through SavedStateHandle
        val returnUrl = RedirectComponent.getReturnUrl(requireActivity().applicationContext) + RETURN_URL_PATH
        arguments = (arguments ?: bundleOf()).apply {
            putString(RETURN_URL_EXTRA, returnUrl)
        }
        _binding = FragmentInstantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { instantViewModel.instantComponentDataFlow.collect(::setupInstantComponent) }
                launch { instantViewModel.events.collect(::onEvent) }
                launch { instantViewModel.instantViewState.collect(::onViewState) }
            }
        }
    }

    fun onNewIntent(intent: Intent) {
        instantPaymentComponent?.handleIntent(intent)
    }

    private fun setupInstantComponent(componentData: InstantComponentData) {
        val instantPaymentComponent = InstantPaymentComponent.PROVIDER.get(
            this,
            componentData.paymentMethod,
            configuration,
            componentData.callback,
        )

        this.instantPaymentComponent = instantPaymentComponent
        binding.componentView.attach(instantPaymentComponent, viewLifecycleOwner)
    }

    private fun onEvent(event: InstantEvent) {
        when (event) {
            is InstantEvent.AdditionalAction -> {
                onAction(event.action)
            }
            is InstantEvent.PaymentResult -> {
                onPaymentResult(event.result)
            }
        }
    }

    private fun onPaymentResult(result: String) {
        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun onViewState(viewState: InstantViewState) {
        when (viewState) {
            is InstantViewState.Error -> {
                binding.errorView.isVisible = true
                binding.errorView.text = viewState.errorMessage
                binding.progressIndicator.isVisible = false
                binding.componentContainer.isVisible = false
            }
            is InstantViewState.Loading -> {
                binding.progressIndicator.isVisible = true
                binding.errorView.isVisible = false
                binding.componentContainer.isVisible = false
            }
            is InstantViewState.ShowComponent -> {
                binding.progressIndicator.isVisible = false
                binding.errorView.isVisible = false
                binding.componentContainer.isVisible = true
            }
        }
    }

    private fun onAction(action: Action) {
        instantPaymentComponent?.handleAction(action, requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instantPaymentComponent = null
        _binding = null
    }

    companion object {

        internal const val TAG = "InstantFragment"
        internal const val PAYMENT_METHOD_TYPE_EXTRA = "PAYMENT_METHOD_TYPE_EXTRA"
        internal const val RETURN_URL_EXTRA = "RETURN_URL_EXTRA"
        internal const val RETURN_URL_PATH = "/instant"

        fun show(fragmentManager: FragmentManager, configuration: InstantPaymentConfiguration, paymentMethodType: String) {
            InstantFragment(configuration).apply {
                arguments = bundleOf(
                    PAYMENT_METHOD_TYPE_EXTRA to paymentMethodType
                )
            }.show(fragmentManager, TAG)
        }

        fun handle(fragmentManager: FragmentManager, action: Action) {
            val fragment = fragmentManager.findFragmentByTag(TAG) as InstantFragment
            fragment.instantViewModel.handle(action)
        }

        fun hide(fragmentManager: FragmentManager) {
            val fragment = fragmentManager.findFragmentByTag(TAG) as InstantFragment
            fragment.dismiss()
        }
    }
}
