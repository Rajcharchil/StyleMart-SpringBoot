// Header Loader Utility
// This script loads the unified header into any page

class HeaderLoader {
    constructor() {
        this.headerContainer = null;
        this.headerLoaded = false;
    }

    // Load header into a specific container
    async loadHeader(containerSelector = '#header-container') {
        try {
            // Find or create header container
            this.headerContainer = document.querySelector(containerSelector);
            if (!this.headerContainer) {
                this.headerContainer = document.createElement('div');
                this.headerContainer.id = 'header-container';
                document.body.insertBefore(this.headerContainer, document.body.firstChild);
            }

            // Load header HTML
            const response = await fetch('/header.html');
            if (!response.ok) {
                throw new Error('Failed to load header');
            }

            const headerHTML = await response.text();
            this.headerContainer.innerHTML = headerHTML;

            // Extract and apply styles
            this.extractAndApplyStyles(headerHTML);

            // Extract and execute scripts
            this.extractAndExecuteScripts(headerHTML);

            this.headerLoaded = true;
            console.log('Header loaded successfully');

            // Trigger custom event
            document.dispatchEvent(new CustomEvent('headerLoaded'));

        } catch (error) {
            console.error('Error loading header:', error);
            this.showFallbackHeader();
        }
    }

    // Extract CSS from header HTML and apply it
    extractAndApplyStyles(headerHTML) {
        const styleMatch = headerHTML.match(/<style>([\s\S]*?)<\/style>/);
        if (styleMatch) {
            const styleContent = styleMatch[1];
            const styleElement = document.createElement('style');
            styleElement.textContent = styleContent;
            document.head.appendChild(styleElement);
        }
    }

    // Extract JavaScript from header HTML and execute it
    extractAndExecuteScripts(headerHTML) {
        const scriptMatch = headerHTML.match(/<script>([\s\S]*?)<\/script>/);
        if (scriptMatch) {
            const scriptContent = scriptMatch[1];
            const scriptElement = document.createElement('script');
            scriptElement.textContent = scriptContent;
            document.head.appendChild(scriptElement);
        }
    }

    // Show fallback header if loading fails
    showFallbackHeader() {
        const fallbackHTML = `
            <header style="background: #fff; padding: 1rem; box-shadow: 0 1px 4px rgba(0,0,0,.08);">
                <div style="max-width: 1380px; margin: auto; display: flex; justify-content: space-between; align-items: center;">
                    <div style="font-size: 1.5rem; font-weight: bold; color: #ff3f6c;">StyleMart</div>
                    <nav>
                        <a href="/" style="margin: 0 1rem; text-decoration: none; color: #333;">Home</a>
                        <a href="/products" style="margin: 0 1rem; text-decoration: none; color: #333;">Products</a>
                        <a href="/cart" style="margin: 0 1rem; text-decoration: none; color: #333;">Cart</a>
                    </nav>
                </div>
            </header>
        `;
        
        if (this.headerContainer) {
            this.headerContainer.innerHTML = fallbackHTML;
        }
    }

    // Update cart count (can be called from other pages)
    updateCartCount() {
        if (this.headerLoaded && window.headerFunctions) {
            window.headerFunctions.updateCartCount();
        }
    }

    // Get cart data (can be called from other pages)
    getCart() {
        if (this.headerLoaded && window.headerFunctions) {
            return window.headerFunctions.getCart();
        }
        return [];
    }

    // Save cart data (can be called from other pages)
    saveCart(cart) {
        if (this.headerLoaded && window.headerFunctions) {
            window.headerFunctions.saveCart(cart);
        }
    }
}

// Global instance
window.headerLoader = new HeaderLoader();

// After header is loaded, inject user info if available
function injectUserInfoToProfile() {
    // For demo: get from localStorage (replace with real auth in production)
    const name = localStorage.getItem('userName');
    const email = localStorage.getItem('userEmail');
    const nameElem = document.getElementById('profileUserName');
    const emailElem = document.getElementById('profileUserEmail');
    const loginBtn = document.getElementById('profileLoginBtn');
    if (name && email) {
        if (nameElem) nameElem.textContent = name;
        if (emailElem) emailElem.textContent = email;
        if (loginBtn) loginBtn.style.display = 'none';
    } else {
        if (nameElem) nameElem.textContent = 'Welcome';
        if (emailElem) emailElem.textContent = 'To access account and manage orders';
        if (loginBtn) loginBtn.style.display = 'block';
    }
}

// Auto-load header when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Check if header is already present
    const existingHeader = document.querySelector('.unified-header');
    if (!existingHeader) {
        window.headerLoader.loadHeader();
    }
});

document.addEventListener('headerLoaded', injectUserInfoToProfile);
document.addEventListener('DOMContentLoaded', injectUserInfoToProfile);

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = HeaderLoader;
} 