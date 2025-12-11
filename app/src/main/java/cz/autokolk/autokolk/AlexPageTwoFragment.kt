package cz.autokolk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.Toast

class AlexPageTwoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alex_page_two, container, false)

        fun handlePurchase(hungerDelta: Int, pointCost: Int, successMsg: String) {
            val ctx = context ?: return
            val progress = LessonProgress(ctx)
            val hunger = HungerManager(ctx)
            val currentHunger = hunger.getCurrentHunger()
            if (currentHunger + hungerDelta > HungerManager.MAX_HUNGER) {
                Toast.makeText(ctx, "Nelze přes 100 🍖", Toast.LENGTH_SHORT).show()
                return
            }
            val ok = progress.spendPoints(pointCost)
            if (!ok) {
                Toast.makeText(ctx, "Nedostatek bodů", Toast.LENGTH_SHORT).show()
                return
            }
            val newValue = (currentHunger + hungerDelta).coerceAtMost(HungerManager.MAX_HUNGER)
            hunger.setCurrentHunger(newValue)
            // align decay baseline to now for clearer UX after feeding
            // by touching getCurrentHunger next read with updated last_update in HungerManager
            Toast.makeText(ctx, successMsg, Toast.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("hunger_updated", Bundle.EMPTY)
            // Update top bar points immediately
            (activity as? AlexActivity)?.refreshPointsHeader()
        }

        view.findViewById<LinearLayout>(R.id.food_klobaska)?.setOnClickListener {
            handlePurchase(hungerDelta = 1, pointCost = 4, successMsg = "+ 1 🍖")
        }
        view.findViewById<LinearLayout>(R.id.food_kure)?.setOnClickListener {
            handlePurchase(hungerDelta = 10, pointCost = 30, successMsg = "+ 10 🍖")
        }
        view.findViewById<LinearLayout>(R.id.food_zmrzlina)?.setOnClickListener {
            handlePurchase(hungerDelta = 3, pointCost = 10, successMsg = "+ 3 🍖")
        }
        view.findViewById<LinearLayout>(R.id.food_mrkev)?.setOnClickListener {
            handlePurchase(hungerDelta = 5, pointCost = 16, successMsg = "+ 5 🍖")
        }
        view.findViewById<LinearLayout>(R.id.food_pivo)?.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            val progress = LessonProgress(ctx)
            val hunger = HungerManager(ctx)
            val currentHunger = hunger.getCurrentHunger()
            if (currentHunger >= HungerManager.MAX_HUNGER) {
                Toast.makeText(ctx, "Už na maximu 🍖", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ok = progress.spendPoints(150)
            if (!ok) {
                Toast.makeText(ctx, "Nedostatek bodů", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            hunger.setCurrentHunger(HungerManager.MAX_HUNGER)
            Toast.makeText(ctx, "+ MAX 🍖", Toast.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("hunger_updated", Bundle.EMPTY)
            // Update top bar points immediately
            (activity as? AlexActivity)?.refreshPointsHeader()
        }

        // Kamení: costs 80 points and freezes hunger decay for 48 hours
        view.findViewById<LinearLayout>(R.id.food_kameni)?.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            val progress = LessonProgress(ctx)
            val ok = progress.spendPoints(80)
            if (!ok) {
                Toast.makeText(ctx, "Nedostatek bodů", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val hunger = HungerManager(ctx)
            hunger.freezeDecayForHours(48)
            Toast.makeText(ctx, "Alex nebude 48h hladovět 🪨", Toast.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("hunger_updated", Bundle.EMPTY)
            (activity as? AlexActivity)?.refreshPointsHeader()
        }

        return view
    }
}



