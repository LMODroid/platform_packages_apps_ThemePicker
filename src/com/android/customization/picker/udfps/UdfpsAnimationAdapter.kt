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

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.android.wallpaper.R

import com.libremobileos.providers.LMOSettings

class UdfpsAnimationAdapter private constructor(
    private val context: Context,
    private val animationPackage: String,
    private val animations: List<String>,
    private val animationPreviews: List<String>,
    private val animationTitles: List<String>
) : RecyclerView.Adapter<UdfpsAnimationAdapter.UdfpsAnimationViewHolder>() {

    private var selectedAnim = getSelectedAnimation()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UdfpsAnimationViewHolder {
        return UdfpsAnimationViewHolder.from(parent, this)
    }

    override fun getItemCount(): Int {
        return animations.size
    }

    override fun onBindViewHolder(holder: UdfpsAnimationViewHolder, position: Int) {
        holder.bind()
    }

    private fun getSelectedAnimation(): Int {
        val udfpsStyle = Settings.Secure.getInt(
            context.contentResolver,
            LMOSettings.Secure.UDFPS_ANIM_STYLE, 0
        )
        // return 0 for unsupported values as like we did in SystemUI.
        return if (udfpsStyle < 0 || udfpsStyle >= animations.size) 0 else udfpsStyle
    }

    companion object {
        fun from(context: Context, animationPackage: String): UdfpsAnimationAdapter? {
            return try {
                val packageManager = context.packageManager
                val resources = packageManager.getResourcesForApplication(animationPackage)
                val animations = resources.getStringArray(
                    resources.getIdentifier(
                        "udfps_animation_styles",
                        "array", animationPackage
                    )
                ).toList()
                val animationPreviews = resources.getStringArray(
                    resources.getIdentifier(
                        "udfps_animation_previews",
                        "array", animationPackage
                    )
                ).toList()
                val animationTitles = resources.getStringArray(
                    resources.getIdentifier(
                        "udfps_animation_titles",
                        "array", animationPackage
                    )
                ).toList()
                UdfpsAnimationAdapter(
                    context,
                    animationPackage,
                    animations,
                    animationPreviews,
                    animationTitles
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    class UdfpsAnimationViewHolder private constructor(
        rootView: View,
        private val adapter: UdfpsAnimationAdapter
    ) : RecyclerView.ViewHolder(rootView) {

        private val title: TextView by lazy { itemView.requireViewById(R.id.option_label) }
        private val image: ImageView by lazy { itemView.requireViewById(R.id.option_thumbnail) }

        fun bind() {
            val animIndex = adapterPosition
            // Set the title and thumbnail
            image.setImageDrawable(getDrawable(animIndex, true))
            title.text = adapter.animationTitles[animIndex]
            itemView.isActivated = adapter.selectedAnim == animIndex
            itemView.setOnClickListener {
                val bgDrawable = getDrawable(animIndex, false)
                // No need to animate on none.
                if (bgDrawable != null) {
                    image.background = bgDrawable
                    val animation = image.background as AnimationDrawable
                    animation.isOneShot = true
                    animation.start()
                }
                if (adapter.selectedAnim != animIndex) {
                    val prevSelect = adapter.selectedAnim
                    adapter.selectedAnim = animIndex
                    adapter.notifyItemChanged(prevSelect)
                    adapter.notifyItemChanged(animIndex)
                    // Update settings
                    updateAnimationStyle(animIndex)
                }
            }
        }

        private fun getDrawable(index: Int, preview: Boolean): Drawable? {
            return try {
                val animations = adapter.animations
                val animationsPreviews = adapter.animationPreviews
                val animationPackage = adapter.animationPackage
                val drawableName = if (preview) animationsPreviews[index] else animations[index]
                val pm = adapter.context.packageManager
                val resources: Resources = pm.getResourcesForApplication(animationPackage)
                resources.getDrawable(
                    resources.getIdentifier(
                        drawableName,
                        "drawable",
                        animationPackage
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun updateAnimationStyle(animIndex: Int) {
            Settings.Secure.putInt(
                itemView.context.contentResolver,
                LMOSettings.Secure.UDFPS_ANIM_STYLE, animIndex
            )
        }

        companion object {
            fun from(parent: ViewGroup, adapter: UdfpsAnimationAdapter): UdfpsAnimationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val rootView = layoutInflater.inflate(R.layout.item_udfps_animation, parent, false)
                return UdfpsAnimationViewHolder(rootView, adapter)
            }
        }

    }
}
