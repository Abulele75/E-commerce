/**
 * Menu toggle drawn with CSS bars rather than an icon font or glyph.
 * The bars animate into a close affordance when the drawer is open.
 */
export default function HamburgerButton({ open, controls, onToggle, buttonRef }) {
  return (
    <button
      ref={buttonRef}
      type="button"
      className={`hamburger${open ? ' hamburger--open' : ''}`}
      aria-label={open ? 'Close navigation menu' : 'Open navigation menu'}
      aria-expanded={open}
      aria-controls={controls}
      onClick={onToggle}
    >
      <span className="hamburger__bars" aria-hidden="true"><span /><span /><span /></span>
    </button>
  )
}
