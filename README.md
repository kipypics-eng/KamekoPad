# KamekoPad
Version 0.1.0

KamekoPadは、イベントやライブ等での撮影向け写真管理・SNS投稿支援Androidアプリです。
機材情報や撮影設定（EXIF）に基づいた整理を行い、SNSへのスムーズな投稿をサポートすることを目的としています。

## 機能概要
- EXIF情報の自動抽出（ISO、シャッタースピード、絞り、焦点距離）
- イベント（現場）単位での写真管理と自動紐付け
- 24時間以内の投稿数や過去のイベント履歴の集計・可視化
- X (Twitter) や Adobe Lightroom への直接共有機能
- 複数テーマの切り替え

## 技術仕様
- 言語: Kotlin
- UIフレームワーク: Jetpack Compose
- データベース: Room (SQLite)
- 画像処理: Coil
- アーキテクチャ: ViewModel, StateFlow, SavedStateHandle

## 動作環境
- 推奨環境: Android Studio Ladybug (2024.2.1) 以降
- Target SDK: 36
- Min SDK: 26

## ライセンス
[MIT License](LICENSE)
