package com.subhashana.spendwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var skipButton: Button
    private lateinit var nextButton: Button
    private lateinit var backButton: Button
    private lateinit var indicatorContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        initializeViews()
        setupViewPager()
        setupClickListeners()
    }

    private fun initializeViews() {
        viewPager = findViewById(R.id.viewPager)
        skipButton = findViewById(R.id.skipButton)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        indicatorContainer = findViewById(R.id.indicatorContainer)
    }

    private fun setupViewPager() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_track_expenses,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_description_1)
            ),
            OnboardingItem(
                R.drawable.ic_set_budget,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_description_2)
            ),
            OnboardingItem(
                R.drawable.ic_analytics,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_description_3)
            )
        )

        viewPager.adapter = OnboardingAdapter(onboardingItems)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons(position)
            }
        })
    }

    private fun setupClickListeners() {
        skipButton.setOnClickListener {
            // Go to the last onboarding screen
            viewPager.currentItem = (viewPager.adapter?.itemCount ?: 0) - 1
        }

        backButton.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        nextButton.setOnClickListener {
            if (viewPager.currentItem + 1 < viewPager.adapter?.itemCount ?: 0) {
                viewPager.currentItem += 1
            } else {
                startPasscodeActivity()
            }
        }
    }

    private fun updateButtons(position: Int) {
        // Show skip button only on the first screen
        skipButton.visibility = if (position == 0) View.VISIBLE else View.GONE
        
        // Show back button on second and third screens
        backButton.visibility = if (position > 0) View.VISIBLE else View.GONE

        // Update next button text
        if (position == (viewPager.adapter?.itemCount ?: 0) - 1) {
            nextButton.text = getString(R.string.get_started)
        } else {
            nextButton.text = getString(R.string.next)
        }
    }

    private fun startPasscodeActivity() {
        val intent = Intent(this, PasscodeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

data class OnboardingItem(
    val imageResId: Int,
    val title: String,
    val description: String
)

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_screen, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OnboardingViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageOnboarding)
        private val titleView: TextView = itemView.findViewById(R.id.titleOnboarding)
        private val descriptionView: TextView = itemView.findViewById(R.id.descriptionOnboarding)

        fun bind(item: OnboardingItem) {
            imageView.setImageResource(item.imageResId)
            titleView.text = item.title
            descriptionView.text = item.description
        }
    }
} 