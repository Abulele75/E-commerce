import { motion, useReducedMotion } from 'motion/react'
import { staggerContainerVariants, viewportOnce } from '../../../constants/motion/motionTokens.js'

/**
 * Sequences direct children that are rendered as StaggerItem.
 * Use for grids and lists so rows arrive in a natural wave instead of all at once.
 */
export default function StaggerGroup({ as = 'div', className, children, ...rest }) {
  const reduceMotion = useReducedMotion()
  const Tag = motion[as] || motion.div

  if (reduceMotion) return <Tag className={className} {...rest}>{children}</Tag>

  return (
    <Tag
      className={className}
      variants={staggerContainerVariants}
      initial="hidden"
      whileInView="visible"
      viewport={viewportOnce}
      {...rest}
    >
      {children}
    </Tag>
  )
}
