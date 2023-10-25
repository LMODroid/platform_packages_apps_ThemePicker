/*
 * Copyright (C) 2023 The LibreMobileOS Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.customization.model.udfps

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater

import com.android.customization.picker.udfps.UdfpsAnimationFragment
import com.android.customization.picker.udfps.UdfpsAnimationSectionView
import com.android.wallpaper.R
import com.android.wallpaper.model.CustomizationSectionController
import com.android.wallpaper.model.CustomizationSectionController.CustomizationSectionNavigationController

class UdfpsAnimationSectionController(
    private val sectionNavigationController: CustomizationSectionNavigationController
) : CustomizationSectionController<UdfpsAnimationSectionView> {

    override fun isAvailable(context: Context): Boolean {
        val enabled = context.resources
                .getBoolean(R.bool.config_show_udfps_animation_customization)
        return enabled && isUdfpsAvailable(context) && isUdfpsAnimationPackageInstalled(context)
    }

    override fun createView(context: Context): UdfpsAnimationSectionView {
        val sectionView = LayoutInflater.from(context).inflate(
                R.layout.udfps_animation_section_view, /* root= */ null)
        sectionView.setOnClickListener {
            launchUdfpsAnimationFragment()
        }
        return sectionView as UdfpsAnimationSectionView
    }

    private fun launchUdfpsAnimationFragment() {
        val fragment = UdfpsAnimationFragment()
        sectionNavigationController.navigateTo(fragment)
    }

    private fun isUdfpsAvailable(context: Context): Boolean {
        return context.resources.getIntArray(
                com.android.internal.R.array.config_udfps_sensor_props).isNotEmpty()
    }

    private fun isUdfpsAnimationPackageInstalled(context: Context): Boolean {
        val animationPackage = context.getString(
            com.android.internal.R.string.config_udfps_animation_customization_package
        )
        return isPackageInstalled(context, animationPackage)
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0 /* flags */) != null
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}
