import { motion, useReducedMotion } from 'motion/react'
import { revealVariants, viewportOnce } from '../../../constants/motion/motionTokens.js'

/**
 * Reveals a block once it scrolls into view.
 *
 * When the user prefers reduced motion the element renders statically, so content is
 * never hidden behind an animation that will not run.
 */
export default function Reveal({ as = 'div', delay = 0, className, children, ...rest }) {
  const reduceMotion = useReducedMotion()
  const Tag = motion[as] || motion.div

  if (reduceMotion) return <Tag className={className} {...rest}>{children}</Tag>

  return (
    <Tag
      className={className}
      variants={revealVariants}
      initial="hidden"
      whileInView="visible"
      viewport={viewportOnce}
      transition={{ delay }}
      {...rest}
    >
      {children}
    </Tag>
  )
}
