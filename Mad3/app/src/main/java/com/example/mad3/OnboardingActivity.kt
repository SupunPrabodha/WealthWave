package com.example.mad3

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.mad3.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var indicatorContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
        
        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == onboardingAdapter.itemCount - 1) {
                    binding.buttonNext.text = "Get Started"
                    binding.buttonSkip.text = "Back"
                } else {
                    binding.buttonNext.text = "Next"
                    binding.buttonSkip.text = "Skip"
                }
            }
        })

        binding.buttonNext.setOnClickListener {
            if (binding.onboardingViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.onboardingViewPager.currentItem += 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.buttonSkip.setOnClickListener {
            if (binding.onboardingViewPager.currentItem == onboardingAdapter.itemCount - 1) {
                if (binding.onboardingViewPager.currentItem > 0) {
                    binding.onboardingViewPager.currentItem -= 1
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding1,
                "Track Your Expenses",
                "Keep track of your spending habits and manage your finances effectively"
            ),
            OnboardingItem(
                R.drawable.onboarding2,
                "Set Budgets",
                "Create budgets for different categories and stay within your limits"
            ),
            OnboardingItem(
                R.drawable.onboarding3,
                "Financial Insights",
                "Get detailed insights and analytics about your spending patterns"
            )
        )
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.onboardingViewPager.adapter = onboardingAdapter
    }

    private fun setupIndicators() {
        indicatorContainer = binding.indicatorsContainer
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
} 