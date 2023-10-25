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

package com.android.customization.picker.udfps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

import com.android.wallpaper.R
import com.android.wallpaper.picker.AppbarFragment

class UdfpsAnimationFragment : AppbarFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.layout_udfps_animation, container, false)
        setUpToolbar(fragmentView)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.requireViewById<RecyclerView>(R.id.recycler_view)
        val animationPackage = getString(
                com.android.internal.R.string.config_udfps_animation_customization_package)
        val udfpsAnimationAdapter = UdfpsAnimationAdapter.from(requireContext(), animationPackage)
        recyclerView.adapter = udfpsAnimationAdapter
    }

    override fun getDefaultTitle(): CharSequence {
        return getString(R.string.udfps_animation_title)
    }

}
