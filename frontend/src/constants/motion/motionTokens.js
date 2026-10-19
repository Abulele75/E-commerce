/**
 * Shared motion tokens.
 *
 * Timings follow the enterprise guidance applied to this storefront: micro-interactions
 * stay inside the 150-300ms band so movement reads as responsive rather than decorative.
 * Every consumer must also honour `prefers-reduced-motion`.
 */

export const duration = {
  fast: 0.16,
  base: 0.24,
  slow: 0.34,
}

/** Standard decelerating curve used for entrances. */
export const easeOut = [0.22, 0.61, 0.36, 1]

/** Symmetric curve used for hover and state changes. */
export const easeInOut = [0.4, 0, 0.2, 1]

export const transition = { duration: duration.base, ease: easeOut }

/** Single element entrance: fade with a short upward travel. */
export const revealVariants = {
  hidden: { opacity: 0, y: 16 },
  visible: { opacity: 1, y: 0, transition },
}

/** Parent container that sequences its children. */
export const staggerContainerVariants = {
  hidden: {},
  visible: { transition: { staggerChildren: 0.06, delayChildren: 0.04 } },
}

/** Child of a stagger container. Scale stays subtle to avoid a bouncy, sloppy feel. */
export const staggerItemVariants = {
  hidden: { opacity: 0, y: 14, scale: 0.985 },
  visible: { opacity: 1, y: 0, scale: 1, transition },
}

/** Route level crossfade. Exit is faster than enter so navigation feels immediate. */
export const pageVariants = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0, transition: { duration: duration.base, ease: easeOut } },
  exit: { opacity: 0, y: -6, transition: { duration: duration.fast, ease: easeInOut } },
}

/** Shared viewport config so reveals trigger once, slightly before full visibility. */
export const viewportOnce = { once: true, amount: 0.18 }
