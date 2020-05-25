# Copyright (C) 2019 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := frameworks-base-overlays
LOCAL_REQUIRED_MODULES := \
	AccentColorDarkOrangeOverlay \
	AccentColorMIUIOverlay \
        AccentColorNextbitOverlay \
	AccentColorOnePlusOverlay \
	AccentColorParanoidOverlay \
        AccentColorShapeShifterOverlay \
	AccentColorPepsiOverlay \
	AccentColorTealOverlay \
	AccentColorRedOverlay \
	AccentColorQGreenOverlay \
	AccentColorPinkOverlay \
	AccentColorLightPurpleOverlay \
	AccentColorIndigoOverlay \
	AccentColorFlatPinkOverlay \
	AccentColorCyanOverlay \
	AccentColorBlueGrayOverlay \
        AccentColorCocaColaOverlay \
	AccentColorDiscordOverlay \
	AccentColorGoldenShowerOverlay \
	AccentColorJollibeeOverlay \
        AccentColorRazerOverlay \
        AccentColorStarbucksOverlay \
	AccentColorUbuntuOverlay \
	AccentColorMatrixOverlay \
	AccentColorSalmonOverlay \
	AccentColorMetallicGoldOverlay \
	AccentColorInfernoRedOverlay \
	AccentColorDorsetGoldOverlay \
	AccentColorXboxOverlay \
	AccentColorXiaomiOverlay \
	AccentColorBlackOverlay \
	AccentColorCinnamonOverlay \
	AccentColorOceanOverlay \
	AccentColorOrchidOverlay \
	AccentColorSpaceOverlay \
	AccentColorGreenOverlay \
	AccentColorPurpleOverlay \
	DisplayCutoutEmulationCornerOverlay \
	DisplayCutoutEmulationDoubleOverlay \
	DisplayCutoutEmulationTallOverlay \
	FontGoogleSansOverlay \
	FontCircularStdOverlay \
	FontOnePlusSlateOverlay \
    FontBigNoodle \
    FontBikoHanken \
    FontComicNeue \
    FontExo2 \
    FontFinlandica \
    FontGoodlight \
    FontGravity \
    FontInter \
    FontLeagueMonoNarrow \
    FontLeonSans \
    FontMescla \
    FontMittelschrift \
    FontOdibee \
    FontPanamericana \
    FontPissel \
    FontPTSansMono \
    FontReemKufi \
    FontRoboto \
    FontRouterGothicNarrow \
    FontRoutedGothicRobotoCondensed \
    FontSofiaSans \
    FontSofiaSansSemiCondensed \
	IconPackCircularAndroidOverlay \
	IconPackCircularLauncherOverlay \
	IconPackCircularSettingsOverlay \
	IconPackCircularSystemUIOverlay \
	IconPackCircularThemePickerOverlay \
	IconPackFilledAndroidOverlay \
	IconPackFilledLauncherOverlay \
	IconPackFilledSettingsOverlay \
	IconPackFilledSystemUIOverlay \
	IconPackFilledThemePickerOverlay \
	IconPackRoundedAndroidOverlay \
	IconPackRoundedLauncherOverlay \
	IconPackRoundedSettingsOverlay \
	IconPackRoundedSystemUIOverlay \
	IconPackRoundedThemePickerOverlay \
        IconShapeCylinderOverlay \
	IconShapeHexagonOverlay \
	IconShapeRoundedHexagonOverlay \
	IconShapeRoundedRectOverlay \
	IconShapeSquircleOverlay \
	IconShapeTeardropOverlay \
	PrimaryColorOceanOverlay \
	PrimaryColorNatureOverlay \
        PrimaryColorDarkBlueOverlay \
        PrimaryColorAlmostBlackOverlay \
        PrimaryColorBlissOverlay \
        PrimaryColorBlissClearOverlay \
        PrimaryColorEyeSootherOverlay \
        PrimaryColorOneplusDarkOverlay \
	NavigationBarMode3ButtonOverlay \
	NavigationBarMode2ButtonOverlay \
	NavigationBarModeGesturalOverlay \
	NavigationBarModeGesturalOverlayNarrowBack \
	NavigationBarModeGesturalOverlayWideBack \
	NavigationBarModeGesturalOverlayExtraWideBack \
	PrimaryColorBlackOverlay

include $(BUILD_PHONY_PACKAGE)
include $(CLEAR_VARS)

LOCAL_MODULE := frameworks-base-overlays-debug

include $(BUILD_PHONY_PACKAGE)
include $(call first-makefiles-under,$(LOCAL_PATH))
