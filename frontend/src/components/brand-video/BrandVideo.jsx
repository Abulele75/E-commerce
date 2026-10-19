import { useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { useReducedMotion } from 'motion/react'
import { storeAssets } from '../../constants/assets/storeAssets.js'

/**
 * Brand film section.
 *
 * The source file is served untouched. It is never rendered wider than its native
 * 1280px and uses object-fit: contain, so there is no upscaling and no cropping.
 * Autoplay is muted (browser policy) and skipped entirely under reduced-motion.
 */
export default function BrandVideo() {
  const videoRef = useRef(null)
  const reduceMotion = useReducedMotion()
  const [playing, setPlaying] = useState(!reduceMotion)
  const [muted, setMuted] = useState(true)

  const togglePlay = () => {
    const video = videoRef.current
    if (!video) return
    if (video.paused) { video.play(); setPlaying(true) } else { video.pause(); setPlaying(false) }
  }

  const toggleSound = () => {
    const video = videoRef.current
    if (!video) return
    video.muted = !video.muted
    setMuted(video.muted)
  }

  return (
    <section className="brand-video" aria-labelledby="brand-video-title">
      <div className="brand-video__inner">
        <div className="brand-video__copy">
          <span className="eyebrow">Who we are</span>
          <h2 id="brand-video-title">Built to keep up.</h2>
          <Link className="button button--primary" to="/catalog">Explore the range</Link>
        </div>

        <figure className="brand-video__frame">
          <video
            ref={videoRef}
            className="brand-video__player"
            src={storeAssets.brandVideo}
            width={storeAssets.brandVideoWidth}
            height={storeAssets.brandVideoHeight}
            autoPlay={!reduceMotion}
            muted
            loop
            playsInline
            preload="metadata"
            aria-label="Incredible Tech brand film"
            onPlay={() => setPlaying(true)}
            onPause={() => setPlaying(false)}
          />
          <figcaption className="brand-video__controls">
            <button type="button" className="brand-video__control" onClick={togglePlay}>{playing ? 'Pause' : 'Play'}</button>
            <button type="button" className="brand-video__control" onClick={toggleSound} aria-pressed={!muted}>{muted ? 'Sound on' : 'Mute'}</button>
          </figcaption>
        </figure>
      </div>
    </section>
  )
}
