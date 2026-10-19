import { motion, useReducedMotion } from 'motion/react'
import { staggerItemVariants } from '../../../constants/motion/motionTokens.js'

/**
 * Child of a StaggerGroup. Inherits the parent timeline, so it needs no viewport config.
 */
export default function StaggerItem({ as = 'div', className, children, ...rest }) {
  const reduceMotion = useReducedMotion()
  const Tag = motion[as] || motion.div

  if (reduceMotion) return <Tag className={className} {...rest}>{children}</Tag>

  return (
    <Tag className={className} variants={staggerItemVariants} {...rest}>
      {children}
    </Tag>
  )
}
