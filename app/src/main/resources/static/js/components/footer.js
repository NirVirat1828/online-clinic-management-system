/**
 * Footer rendering module.
 *
 * Dynamically builds and injects a consistent footer across pages.
 *
 * Features / Improvements over plain innerHTML:
 *  - Programmatic DOM creation (safer & easier to extend).
 *  - Graceful fallback if #footer container is missing (auto-creates it).
 *  - Easily extendable: add / remove columns via a configuration array.
 *  - Accessible structure with nav landmarks and descriptive alt text.
 *  - Optional year auto-update.
 *
 * Usage:
 *  - Import (ESM): import { renderFooter } from "./footer.js"; renderFooter();
 *  - Or rely on auto-execution after DOMContentLoaded (included below).
 *
 * Assumptions:
 *  - Logo asset located at ../assets/images/logo/logo.png (adjust if needed).
 */

const FOOTER_LINK_GROUPS = [
  {
    title: "Company",
    links: [
      { label: "About", href: "#" },
      { label: "Careers", href: "#" },
      { label: "Press", href: "#" }
    ]
  },
  {
    title: "Support",
    links: [
      { label: "Account", href: "#" },
      { label: "Help Center", href: "#" },
      { label: "Contact Us", href: "#" }
    ]
  },
  {
    title: "Legals",
    links: [
      { label: "Terms & Conditions", href: "#" },
      { label: "Privacy Policy", href: "#" },
      { label: "Licensing", href: "#" }
    ]
  }
];

/**
 * Ensure there is a mount element with id="footer".
 */
function ensureFooterMountPoint() {
  let mount = document.getElementById("footer");
  if (!mount) {
    mount = document.createElement("div");
    mount.id = "footer";
    document.body.appendChild(mount);
  }
  return mount;
}

/**
 * Builds the footer element structure.
 * @returns {HTMLElement} <footer> root
 */
function buildFooter() {
  const footer = document.createElement("footer");
  footer.className = "footer";

  const container = document.createElement("div");
  container.className = "footer-container";

  // Logo + copyright
  const logoWrapper = document.createElement("div");
  logoWrapper.className = "footer-logo";

  const logoImg = document.createElement("img");
  logoImg.src = "../assets/images/logo/logo.png"; // Adjust path if needed
  logoImg.alt = "Hospital CMS Logo";

  const year = new Date().getFullYear();
  const copy = document.createElement("p");
  copy.textContent = `© Copyright ${year}. All Rights Reserved by Hospital CMS.`;

  logoWrapper.appendChild(logoImg);
  logoWrapper.appendChild(copy);

  // Links Section
  const linksWrapper = document.createElement("div");
  linksWrapper.className = "footer-links";
  linksWrapper.setAttribute("role", "navigation");
  linksWrapper.setAttribute("aria-label", "Footer");

  FOOTER_LINK_GROUPS.forEach(group => {
    const col = document.createElement("div");
    col.className = "footer-column";

    const heading = document.createElement("h4");
    heading.textContent = group.title;
    col.appendChild(heading);

    group.links.forEach(linkObj => {
      const a = document.createElement("a");
      a.href = linkObj.href;
      a.textContent = linkObj.label;
      a.rel = determineRel(linkObj.href);
      col.appendChild(a);
    });

    linksWrapper.appendChild(col);
  });

  container.appendChild(logoWrapper);
  container.appendChild(linksWrapper);
  footer.appendChild(container);

  return footer;
}

/**
 * Determine a safe rel attribute for external links.
 * @param {string} href
 * @returns {string|undefined}
 */
function determineRel(href) {
  if (!href || href === "#" || href.startsWith("/")) return undefined;
  // External link
  return "noopener noreferrer";
}

/**
 * Public API: Render / re-render the footer.
 */
export function renderFooter() {
  const mount = ensureFooterMountPoint();
  const footerEl = buildFooter();
  mount.innerHTML = "";
  mount.appendChild(footerEl);
}

/**
 * Optional: Expose globally for non-module usage.
 */
(function exposeGlobal() {
  if (typeof window !== "undefined") {
    if (!window.renderFooter) {
      window.renderFooter = renderFooter;
    }
  }
})();

/**
 * Auto-render on DOM ready (can be removed if you prefer manual control).
 */
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => renderFooter());
} else {
  renderFooter();
}