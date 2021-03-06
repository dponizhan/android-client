package org.tokend.template.extensions

import android.annotation.SuppressLint
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView

@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShifting() {
    val menuView = (getChildAt(0) as? BottomNavigationMenuView)
            ?: return

    try {
        menuView::class.java.getDeclaredField("mShiftingMode").apply {
            isAccessible = true
            setBoolean(menuView, false)
            isAccessible = false
        }

        (0 until menuView.childCount)
                .mapNotNull { menuView.getChildAt(it) as? BottomNavigationItemView }
                .forEach { menuItem ->
                    menuItem.setShifting(false)
                    menuItem.setChecked(menuItem.itemData.isChecked)
                }
    } catch (e: Exception) {
        // Ok, never mind.
    }
}