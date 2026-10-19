import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { useReducedMotion } from 'motion/react'
import StaggerGroup from '../motion/stagger-group/StaggerGroup.jsx'
import StaggerItem from '../motion/stagger-item/StaggerItem.jsx'
import { storeAssets } from '../../constants/assets/storeAssets.js'
import './home-bento-hero.css'

const heroVideos = storeAssets.heroVideos

export default function HomeBentoHero({ session, firstName }) {
  const reduceMotion = useReducedMotion()
  const [slide, setSlide] = useState(0)
  const videoRefs = useRef([])
  const go = (direction) => setSlide((current) => (current + direction + heroVideos.length) % heroVideos.length)

  // Only the active slide plays; the rest are paused to keep the page light.
  useEffect(() => {
    videoRefs.current.forEach((video, index) => {
      if (!video) return
      if (index === slide && !reduceMotion) { video.play().catch(() => {}) }
      else { video.pause() }
    })
  }, [slide, reduceMotion])

  return (
    <section className="home-bento" aria-labelledby="home-bento-title">
      <div className="home-bento__shell">
        <StaggerGroup className="home-bento__grid">
          <StaggerItem className="home-bento__item home-bento__item--lead">
            <article className="home-bento__card home-bento__lead">
              <div className="home-bento__slides" aria-live="polite">
                {heroVideos.map((src, index) => (
                  <div className={`home-bento__slide${slide === index ? ' is-active' : ''}`} aria-hidden={slide !== index} key={src}>
                    <video ref={(node) => { videoRefs.current[index] = node }} className="home-bento__lead-video" src={src} muted loop playsInline preload="metadata" tabIndex={-1} aria-hidden="true" />
                  </div>
                ))}
              </div>
              <div className="home-bento__lead-shade" aria-hidden="true" />
              <div className="home-bento__lead-copy">
                <span className="home-bento__eyebrow">Incredible Tech</span>
                <h1 id="home-bento-title">Technology for the way <em>you live.</em></h1>
                <div className="home-bento__actions"><Link className="button button--primary" to="/catalog">Shop now</Link>{session && <Link className="home-bento__text-link" to="/orders">My orders</Link>}</div>
              </div>
              <div className="home-bento__gallery-nav">
                <button type="button" className="home-bento__arrow" aria-label="Previous slide" onClick={() => go(-1)}><span aria-hidden="true" className="home-bento__chevron home-bento__chevron--left" /></button>
                <button type="button" className="home-bento__arrow" aria-label="Next slide" onClick={() => go(1)}><span aria-hidden="true" className="home-bento__chevron home-bento__chevron--right" /></button>
              </div>
            </article>
          </StaggerItem>
          <StaggerItem className="home-bento__item home-bento__item--watch">
            <Link className="home-bento__card home-bento__watch" to="/catalog?category=SMART_WATCHES" aria-label="Explore smart watches">
              <img src={storeAssets.bentoLady} alt="Woman checking an Incredible Tech smart watch" fetchPriority="high" />
              <div className="home-bento__watch-shade" aria-hidden="true" />
              <div className="home-bento__watch-copy"><span className="home-bento__eyebrow">Smart watches</span><h2>Move smarter.</h2><span className="home-bento__inline-action">Discover watches</span></div>
            </Link>
          </StaggerItem>
        </StaggerGroup>
      </div>
    </section>
  )
}