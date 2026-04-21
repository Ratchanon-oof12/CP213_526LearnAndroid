package com.example.financialassistant.data

import kotlinx.coroutines.flow.first
import java.util.Calendar
import kotlin.random.Random

object SeedData {
    suspend fun generate12MonthsMockData(repo: FinancialRepository) {
        val categories = repo.getAllCategories().first()
        if (categories.isEmpty()) return
        
        val expenseCats = categories.filter { it.name != "Salary" && it.name != "Income" && it.name != "Other" }
        val incomeCat = categories.find { it.name == "Salary" } ?: categories.first()
        
        val random = Random(42) // Fixed seed for demonstration predictability
        val calendar = Calendar.getInstance()
        
        // Go back 11 full months + current month (12 months total)
        calendar.add(Calendar.MONTH, -11)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val now = Calendar.getInstance()
        
        while (calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) || 
               (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) <= now.get(Calendar.MONTH))) {
            
            // 1. One Income per month
            repo.insertTransaction(
                Transaction(
                    amount = 35000.0 + random.nextDouble(0.0, 5000.0),
                    type = TransactionType.INCOME,
                    categoryId = incomeCat.id,
                    categoryName = incomeCat.name,
                    categoryColor = incomeCat.colorHex,
                    note = "Monthly Salary",
                    date = calendar.timeInMillis
                )
            )
            
            // 2. Multiple random expenses per month (fewer if it's the current month and day is early)
            val expenseCount = random.nextInt(15, 30)
            for (i in 0 until expenseCount) {
                val dayOffset = random.nextInt(0, 28)
                val txCalc = calendar.clone() as Calendar
                txCalc.add(Calendar.DAY_OF_MONTH, dayOffset)
                
                // Do not generate future expenses for the current month
                if (txCalc.after(now)) continue
                
                val cat = expenseCats.random(random)
                repo.insertTransaction(
                    Transaction(
                        amount = random.nextDouble(50.0, 1200.0),
                        type = TransactionType.EXPENSE,
                        categoryId = cat.id,
                        categoryName = cat.name,
                        categoryColor = cat.colorHex,
                        note = "Mock ${cat.name}",
                        date = txCalc.timeInMillis
                    )
                )
            }
            calendar.add(Calendar.MONTH, 1)
        }
    }
}
