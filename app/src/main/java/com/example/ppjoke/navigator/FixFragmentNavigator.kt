package com.example.ppjoke.navigator

import android.content.Context
import android.os.Bundle
import android.util.Log

import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.NavOptions;
import androidx.navigation.NavDestination;
import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavBackStackEntry
import java.lang.Exception;
import java.util.ArrayDeque;

@Navigator.Name("fixFragment") //fix 5: 需要指定1个名字，源码里自带的名字有navigation、activity、fragment、dialog
class FixFragmentNavigator(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val mContainerId: Int
) : FragmentNavigator(
    mContext, mFragmentManager, mContainerId
) {
    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already"
                        + " saved its state"
            )
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        //fix 1: 把类名作为tag，寻找已存在的Fragment
        //（如果想只针对个别fragment进行保活复用，可以在tag上做些标记比如加个前缀）
        var frag = mFragmentManager.findFragmentByTag(className)
        if (null == frag) {
            //不存在，则创建
            frag = instantiateFragment(mContext, mFragmentManager, className, args)
        }
        frag.arguments = args
        val ft = mFragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

//        ft.replace(mContainerId, frag);
        //fix 2: replace换成show和hide
        val fragments = mFragmentManager.fragments
        for (fragment in fragments) {

            ft.hide(fragment!!)
        }
        if (!frag.isAdded) {
            ft.add(mContainerId, frag, className)
        }
        ft.show(frag)
        ft.setPrimaryNavigationFragment(frag)
        @IdRes val destId = destination.id

        //fix 3: mBackStack是私有的，而且没有暴露出来，只能反射获取
        val mBackStack: ArrayDeque<Int>
        try {
            val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            mBackStack = field[this] as ArrayDeque<Int>
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val initialNavigation = mBackStack.isEmpty()
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)
        val isAdded: Boolean = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) {
            if (mBackStack.size > 1) {
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    //fix 4: 从父类那边copy过来即可
    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }

    companion object {
        private const val TAG = "FixFragmentNavigator"
    }
}