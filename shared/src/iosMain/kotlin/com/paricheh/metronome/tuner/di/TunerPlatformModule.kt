import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.core.audio.IosAudioEngine
import com.paricheh.metronome.tuner.ui.tuner.IosPermissionChecker
import com.paricheh.metronome.tuner.ui.tuner.PermissionChecker
import org.koin.dsl.module

val tunerPlatformModule = module {
    single<AudioEngine> { IosAudioEngine() }
    single<PermissionChecker> { IosPermissionChecker() }
}
