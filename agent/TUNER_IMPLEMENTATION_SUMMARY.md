# Chromatic Tuner - Core Implementation Summary

## Architecture Overview

The chromatic tuner follows a Clean Architecture approach with a clear unidirectional pipeline:

1.  **AudioEngine**: Manages the microphone lifecycle and provides a stream of normalized `AudioFrame`s.
2.  **PitchDetector**: Uses the **YIN algorithm** to estimate the fundamental frequency from audio samples.
3.  **FrequencyNormalizer**: Applies **Exponential Moving Average (EMA)** to reduce jitter and smooth the detected frequency.
4.  **Tuner**: Maps the normalized frequency to the nearest musical note based on an **Instrument** and **Temperament**.
5.  **TunerRepository**: Orchestrates the entire pipeline and exposes a `Flow<TunerState>`.

## Data Flow

`Microphone` -> `AudioRecord (Android)` -> `PCM to Float Normalization` -> `AudioFrame` -> `YIN Algorithm` -> `Frequency Smoothing` -> `Nearest Note Mapping` -> `TunerState (Detected/NoSignal/etc.)` -> `UI (Flow)`

## Extension Points

-   **New Instruments**: Implement the `Instrument` interface (e.g., `Guitar`, `Violin`).
-   **New Temperaments**: Implement the `Temperament` interface (e.g., `JustIntonation`, `MeanTone`).
-   **Pitch Detectors**: Replace `YinPitchDetector` with a different algorithm (e.g., FFT-based, HPS) by implementing `PitchDetector`.
-   **Normalizers**: Implement complex smoothing or stability detection logic in `FrequencyNormalizer`.
-   **User Settings**: Thresholds for tuning (Perfect/Warning) can be passed to `ChromaticTuner`.

## Default Configuration

-   **Sample Rate**: 44100 Hz (Standard high-quality audio).
-   **Audio Format**: 16-bit PCM (Widely supported).
-   **YIN Threshold**: 0.10 (Standard balance between accuracy and robustness).
-   **EMA Alpha**: 0.3 (Provides responsive yet smooth frequency tracking).
-   **Tuning Thresholds**:
    -   Perfect: ±5 cents.
    -   Warning: ±15 cents.

## Dependency Graph

Wiring is handled via Koin in `TunerModule.kt` and platform-specific `TunerPlatformModule.kt`:

-   `TunerRepository` depends on `AudioEngine`, `PitchDetector`, `FrequencyNormalizer`, and `Tuner`.
-   `Tuner` depends on `Instrument`.
-   `Instrument` depends on `Temperament`.
-   `AudioEngine` is platform-specific (`AndroidAudioEngine` vs `IosAudioEngine`).

## Known Limitations

-   **iOS Implementation**: Only stub implementation provided for compatibility.
-   **Noise Reduction**: Basic noise handling is part of YIN, but dedicated denoising is not implemented.
-   **Calibration**: Reference frequency is hardcoded to A4 = 440Hz.

## Recommendations for Phase 2

-   Implement a **Spectrum Analyzer** using FFT for visual feedback.
-   Add **Stability Detection** to `FrequencyNormalizer` to ignore transient noises.
-   Allow **Calibration** settings in the repository.
-   Implement **Auto-Correlation** as a fallback for the YIN algorithm in noisy environments.
