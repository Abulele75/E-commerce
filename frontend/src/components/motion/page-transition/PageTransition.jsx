import { useLayoutEffect } from 'react'
import { motion, useReducedMotion } from 'motion/react'
import { pageVariants } from '../../../constants/motion/motionTokens.js'

/**
 * Route-level crossfade. The keyed wrapper mounts after the previous route
 * exits, then resets scroll before the destination paints.
 */
export default function PageTransition({ children }) {
  const reduceMotion = useReducedMotion()

  useLayoutEffect(() => {
    document.documentElement.scrollTop = 0
    document.body.scrollTop = 0
  }, [])

  if (reduceMotion) return children

  return (
    <motion.div variants={pageVariants} initial="initial" animate="animate" exit="exit">
      {children}
    </motion.div>
  )
}
