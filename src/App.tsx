import React, { useState } from 'react';
import { 
  Server, Database, Shield, MapPin, Copy, Check, FileCode, 
  Layers, Terminal, ExternalLink, Cpu, CheckCircle2,
  Lock, ArrowRight, Eye, RefreshCw
} from 'lucide-react';

interface Endpoint {
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  path: string;
  role: 'PUBLIC' | 'CUSTOMER' | 'ADMIN' | 'SUPER_ADMIN';
  description: string;
  bodySample?: string;
  responseSample?: string;
}

const ENDPOINTS: Record<string, Endpoint[]> = {
  'Auth (/api/auth)': [
    {
      method: 'POST',
      path: '/api/auth/register',
      role: 'PUBLIC',
      description: 'Register a new customer account and initialize their shopping cart.',
      bodySample: JSON.stringify({
        email: "priya.sharma@example.com",
        password: "SecretPassword@123",
        fullName: "Priya Sharma",
        phone: "9876543210"
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "User registered successfully",
        data: {
          accessToken: "eyJhbGciOiJIUzI1NiJ9...",
          refreshToken: "eyJhbGciOiJIUzI1NiJ9...",
          tokenType: "Bearer",
          expiresInMs: 86400000,
          userId: 1,
          email: "priya.sharma@example.com",
          fullName: "Priya Sharma",
          role: "CUSTOMER"
        }
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/auth/login',
      role: 'PUBLIC',
      description: 'Authenticate user with email and password to receive JWT tokens.',
      bodySample: JSON.stringify({
        email: "priya.sharma@example.com",
        password: "SecretPassword@123"
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "Login successful",
        data: {
          accessToken: "eyJhbGciOiJIUzI1NiJ9...",
          refreshToken: "eyJhbGciOiJIUzI1NiJ9...",
          tokenType: "Bearer",
          expiresInMs: 86400000,
          userId: 1,
          email: "priya.sharma@example.com",
          fullName: "Priya Sharma",
          role: "CUSTOMER"
        }
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/auth/refresh',
      role: 'PUBLIC',
      description: 'Exchange valid refresh token for a new access token.',
      bodySample: JSON.stringify({
        refreshToken: "eyJhbGciOiJIUzI1NiJ9..."
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "Token refreshed successfully",
        data: {
          accessToken: "eyJhbGciOiJIUzI1NiJ9...",
          refreshToken: "eyJhbGciOiJIUzI1NiJ9...",
          tokenType: "Bearer",
          expiresInMs: 86400000,
          role: "CUSTOMER"
        }
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/auth/admins',
      role: 'SUPER_ADMIN',
      description: 'Create new ADMIN or SUPER_ADMIN account.',
      bodySample: JSON.stringify({
        email: "operations@ibake.in",
        password: "AdminPassword@123",
        fullName: "Operations Lead",
        phone: "9123456780"
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "Admin account registered successfully",
        data: {
          userId: 5,
          email: "operations@ibake.in",
          role: "ADMIN"
        }
      }, null, 2)
    }
  ],
  'Cities (/api/cities)': [
    {
      method: 'GET',
      path: '/api/cities',
      role: 'PUBLIC',
      description: 'Retrieve list of all active delivery cities in India.',
      responseSample: JSON.stringify({
        success: true,
        message: "Active cities retrieved successfully",
        data: [
          { id: 1, name: "Mumbai", state: "Maharashtra", active: true },
          { id: 2, name: "Delhi NCR", state: "Delhi", active: true },
          { id: 3, name: "Bengaluru", state: "Karnataka", active: true }
        ]
      }, null, 2)
    },
    {
      method: 'PATCH',
      path: '/api/cities/{id}/status',
      role: 'SUPER_ADMIN',
      description: 'Globally enable or disable delivery in a city.',
      bodySample: JSON.stringify({ active: false }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "City globally disabled successfully",
        data: { id: 1, name: "Mumbai", active: false }
      }, null, 2)
    }
  ],
  'Products (/api/products)': [
    {
      method: 'GET',
      path: '/api/products?cityId=1&categoryId=1&isVeg=true',
      role: 'PUBLIC',
      description: 'Filter products by city, category, occasion, veg tag. Shows city starting price & active vendors.',
      responseSample: JSON.stringify({
        success: true,
        message: "Products retrieved successfully",
        data: [
          {
            id: 101,
            name: "Dutch Chocolate Truffle Cake",
            description: "Rich dark Belgian chocolate ganache layered with moist sponge. 100% vegetarian.",
            categoryName: "Cakes",
            occasionName: "Birthday",
            isVeg: true,
            weightInGrams: 500,
            startingPrice: 699.00,
            availableVendors: [
              {
                vendorId: 1,
                vendorName: "The Royal Patisserie Bandra",
                cityName: "Mumbai",
                price: 699.00,
                available: true,
                preparationTimeHours: 3
              }
            ]
          }
        ]
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/products',
      role: 'ADMIN',
      description: 'Create a new product in the catalog.',
      bodySample: JSON.stringify({
        name: "Royal Red Velvet Cream Cheese Cake",
        description: "Classic crimson sponge layered with silky imported cream cheese frosting.",
        imageUrl: "https://images.unsplash.com/photo-1586788680434-30d324b2d46f",
        categoryId: 1,
        occasionId: 2,
        isVeg: true,
        weightInGrams: 1000,
        active: true
      }, null, 2)
    }
  ],
  'Vendors & Availability (/api/vendors)': [
    {
      method: 'POST',
      path: '/api/vendors',
      role: 'ADMIN',
      description: 'Register a bakery vendor partner.',
      bodySample: JSON.stringify({
        name: "Sweet Delights Koramangala",
        contactEmail: "hello@sweetdelights.in",
        contactPhone: "9123456781",
        address: "100ft Road, Koramangala, Bengaluru 560034",
        active: true
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/vendors/availability',
      role: 'ADMIN',
      description: 'Assign vendor to fulfill product in a city with specific price, lead time, and stock.',
      bodySample: JSON.stringify({
        vendorId: 1,
        productId: 101,
        cityId: 1,
        price: 699.00,
        available: true,
        stockQuantity: 50,
        preparationTimeHours: 4
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "Vendor assigned to product and city successfully",
        data: {
          id: 501,
          vendorName: "The Royal Patisserie Bandra",
          productName: "Dutch Chocolate Truffle Cake",
          cityName: "Mumbai",
          price: 699.00,
          available: true
        }
      }, null, 2)
    }
  ],
  'Banners (/api/banners)': [
    {
      method: 'GET',
      path: '/api/banners?cityId=1&occasionId=2',
      role: 'PUBLIC',
      description: 'Get promotional banners scoped to city and occasion within active date range.',
      responseSample: JSON.stringify({
        success: true,
        data: [
          {
            id: 1,
            title: "Diwali Sweet Celebration — 20% Off Festive Cakes",
            imageUrl: "https://images.unsplash.com/photo-1509440159596-0249088772ff",
            cityName: "Mumbai",
            occasionName: "Diwali",
            startDate: "2026-10-01T00:00:00",
            endDate: "2026-11-15T23:59:59",
            active: true
          }
        ]
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/banners',
      role: 'ADMIN',
      description: 'Publish a banner scoped to city and occasion.',
      bodySample: JSON.stringify({
        title: "Valentine's Week Red Velvet Special",
        imageUrl: "https://images.unsplash.com/...",
        cityId: 1,
        occasionId: 4,
        startDate: "2026-02-07T00:00:00",
        endDate: "2026-02-15T23:59:59",
        active: true
      }, null, 2)
    }
  ],
  'Cart (/api/cart)': [
    {
      method: 'GET',
      path: '/api/cart',
      role: 'CUSTOMER',
      description: 'Retrieve logged-in user shopping cart with subtotal calculation.',
      responseSample: JSON.stringify({
        success: true,
        data: {
          id: 1,
          cityId: 1,
          cityName: "Mumbai",
          totalItems: 2,
          totalAmount: 1398.00,
          items: [
            {
              id: 1,
              productId: 101,
              productName: "Dutch Chocolate Truffle Cake",
              vendorName: "The Royal Patisserie Bandra",
              quantity: 2,
              unitPrice: 699.00,
              subtotal: 1398.00
            }
          ]
        }
      }, null, 2)
    },
    {
      method: 'POST',
      path: '/api/cart/items',
      role: 'CUSTOMER',
      description: 'Add product from chosen vendor in city to cart.',
      bodySample: JSON.stringify({
        productId: 101,
        vendorId: 1,
        cityId: 1,
        quantity: 1
      }, null, 2)
    }
  ],
  'Orders (/api/orders)': [
    {
      method: 'POST',
      path: '/api/orders',
      role: 'CUSTOMER',
      description: 'Place order from current active cart items.',
      bodySample: JSON.stringify({
        deliveryAddress: "Flat 402, Sea Green Apts, Worli, Mumbai 400018",
        deliveryDate: "2026-09-08",
        deliverySlot: "Evening (5 PM - 9 PM)",
        customerNotes: "Message on cake: Happy 25th Birthday Rhea!"
      }, null, 2),
      responseSample: JSON.stringify({
        success: true,
        message: "Order placed successfully",
        data: {
          id: 9001,
          orderNumber: "IBK-260907-8492",
          cityName: "Mumbai",
          deliveryAddress: "Flat 402, Sea Green Apts, Worli, Mumbai 400018",
          deliveryDate: "2026-09-08",
          deliverySlot: "Evening (5 PM - 9 PM)",
          totalAmount: 1398.00,
          status: "CONFIRMED"
        }
      }, null, 2)
    },
    {
      method: 'GET',
      path: '/api/orders/my-orders',
      role: 'CUSTOMER',
      description: 'View customer personal order history.'
    },
    {
      method: 'GET',
      path: '/api/orders?status=CONFIRMED&cityId=1',
      role: 'ADMIN',
      description: 'Admin view across all orders, filtered by status and city.'
    },
    {
      method: 'PATCH',
      path: '/api/orders/{id}/status',
      role: 'ADMIN',
      description: 'Update order lifecycle state (PENDING, CONFIRMED, BAKING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED).',
      bodySample: JSON.stringify({ status: "BAKING" }, null, 2)
    }
  ]
};

export default function App() {
  const [selectedGroup, setSelectedGroup] = useState<string>('Auth (/api/auth)');
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  const copyToClipboard = (text: string, key: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 2000);
  };

  const getMethodBadgeClass = (method: string) => {
    switch (method) {
      case 'GET': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'POST': return 'bg-orange-500/10 text-orange-400 border-orange-500/30';
      case 'PUT': return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'PATCH': return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
      case 'DELETE': return 'bg-red-500/10 text-red-400 border-red-500/30';
      default: return 'bg-white/10 text-white/70 border-white/20';
    }
  };

  const getRoleBadgeClass = (role: string) => {
    switch (role) {
      case 'SUPER_ADMIN': return 'bg-purple-500/20 text-purple-400 border-purple-500/30 font-bold';
      case 'ADMIN': return 'bg-red-500/20 text-red-400 border-red-500/30 font-bold';
      case 'CUSTOMER': return 'bg-amber-500/20 text-amber-400 border-amber-500/30';
      default: return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
    }
  };

  return (
    <div id="ibake-app-root" className="min-h-screen bg-[#0A0A0B] text-[#F5F5F5] font-sans flex flex-col">
      {/* Top Header */}
      <header id="main-header" className="h-24 border-b border-white/10 flex items-center justify-between px-6 sm:px-10 bg-[#0A0A0B]/90 backdrop-blur sticky top-0 z-50">
        <div className="flex flex-col">
          <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold">System Architecture</span>
          <h1 className="text-3xl sm:text-4xl font-black tracking-tighter italic uppercase text-white">
            IBake <span className="text-white/40">Core API</span>
          </h1>
        </div>

        <div className="flex items-center gap-4 sm:gap-6">
          <div className="text-right hidden sm:block">
            <div className="text-[10px] uppercase tracking-widest text-white/40">Environment</div>
            <div className="text-sm font-mono text-green-400">PROD_RAILWAY_NODE_01</div>
          </div>
          <div className="w-11 h-11 sm:w-12 sm:h-12 rounded-full border border-white/20 flex items-center justify-center bg-white/5">
            <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main id="main-content" className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-8 py-8 space-y-8">
        {/* Architecture & Highlights Section */}
        <section id="architecture-overview" className="border border-white/10 bg-[#0E0E10] p-6 sm:p-8 rounded-none">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            <div className="lg:col-span-5">
              <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold block mb-2">Backend Engine</span>
              <h2 className="text-5xl sm:text-6xl font-black leading-none tracking-tighter mb-4 text-white">
                SPRING<br />BOOT 3.2
              </h2>
              <p className="text-white/50 text-sm leading-relaxed max-w-md">
                Layered Architecture: Controller, Service, Repository. Secure JWT-based session management for Indian market scalability with PostgreSQL via Supabase.
              </p>
              <div className="mt-6 flex items-center gap-2 text-[10px] font-bold text-white/40 uppercase tracking-tighter">
                <span>MAVEN 3.9</span>
                <span className="w-1 h-1 bg-white/30 rounded-full"></span>
                <span>JAVA 17 LTS</span>
                <span className="w-1 h-1 bg-white/30 rounded-full"></span>
                <span>SPRING SECURITY 6</span>
              </div>
            </div>

            <div className="lg:col-span-7 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <div className="text-[10px] uppercase text-orange-500 font-bold mb-2 tracking-widest">Database Connection</div>
                <div className="p-4 bg-white/5 border border-white/10">
                  <div className="text-sm font-mono text-white font-bold">supabase-pg-pool</div>
                  <div className="text-xs text-white/40 mt-1 uppercase font-mono">HikariCP: Max 10 • SSL: Require</div>
                </div>
              </div>

              <div>
                <div className="text-[10px] uppercase text-orange-500 font-bold mb-2 tracking-widest">Auth Strategy</div>
                <div className="p-4 bg-white/5 border border-white/10">
                  <div className="text-sm font-mono text-white font-bold">HS512 / Role-Based ACL</div>
                  <div className="text-xs text-white/40 mt-1 uppercase font-mono text-wrap">CUSTOMER, ADMIN, SUPER_ADMIN</div>
                </div>
              </div>

              <div>
                <div className="text-[10px] uppercase text-orange-500 font-bold mb-2 tracking-widest">City Availability</div>
                <div className="p-4 bg-white/5 border border-white/10">
                  <div className="text-sm font-mono text-white font-bold">VendorProductCity Triad</div>
                  <div className="text-xs text-white/40 mt-1 uppercase font-mono">City-scoped price & stock lead time</div>
                </div>
              </div>

              <div>
                <div className="text-[10px] uppercase text-orange-500 font-bold mb-2 tracking-widest">Deployment Target</div>
                <div className="p-4 bg-white/5 border border-white/10">
                  <div className="text-sm font-mono text-white font-bold">Railway Cloud Container</div>
                  <div className="text-xs text-white/40 mt-1 uppercase font-mono">Docker JRE 17 Multi-Stage</div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* City-Scoped Pricing Demonstration Card */}
        <section id="city-availability-preview" className="border border-white/10 bg-[#0E0E10] p-6 sm:p-8">
          <div className="flex flex-col sm:flex-row sm:items-end justify-between pb-6 border-b border-white/10 gap-3">
            <div>
              <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold block mb-1">
                Dynamic Fulfillment Matrix
              </span>
              <h3 className="text-2xl sm:text-3xl font-black tracking-tight uppercase text-white">
                Dutch Chocolate Truffle Cake
              </h3>
            </div>
            <div className="flex items-center gap-2">
              <span className="px-2.5 py-1 bg-white/10 text-[10px] font-bold tracking-widest uppercase">EGGLESS / VEG</span>
              <span className="px-2.5 py-1 bg-orange-500/20 text-orange-400 text-[10px] font-bold tracking-widest uppercase">500G GOURMET</span>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
            <div className="group p-5 border border-white/10 hover:border-orange-500/50 bg-white/[0.02] transition-colors">
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm font-bold uppercase tracking-wider text-white flex items-center gap-1.5">
                  <MapPin className="w-4 h-4 text-orange-500" /> Mumbai
                </span>
                <span className="text-[9px] px-1.5 py-0.5 bg-emerald-500/20 text-emerald-400 font-mono font-bold">ACTIVE</span>
              </div>
              <div className="text-xs text-white/40">Vendor: The Royal Patisserie Bandra</div>
              <div className="mt-4 flex items-baseline justify-between">
                <div className="text-3xl font-black italic text-orange-500">₹699</div>
                <span className="text-[11px] font-mono text-white/40 uppercase">Lead: 3 hrs</span>
              </div>
              <div className="mt-3 text-[10px] font-mono text-white/30 border-t border-white/5 pt-2">
                Delivers to Bandra, Andheri, Colaba, Worli
              </div>
            </div>

            <div className="group p-5 border border-white/10 hover:border-orange-500/50 bg-white/[0.02] transition-colors">
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm font-bold uppercase tracking-wider text-white flex items-center gap-1.5">
                  <MapPin className="w-4 h-4 text-orange-500" /> Bengaluru
                </span>
                <span className="text-[9px] px-1.5 py-0.5 bg-emerald-500/20 text-emerald-400 font-mono font-bold">ACTIVE</span>
              </div>
              <div className="text-xs text-white/40">Vendor: Sweet Delights Koramangala</div>
              <div className="mt-4 flex items-baseline justify-between">
                <div className="text-3xl font-black italic text-orange-500">₹649</div>
                <span className="text-[11px] font-mono text-white/40 uppercase">Lead: 4 hrs</span>
              </div>
              <div className="mt-3 text-[10px] font-mono text-white/30 border-t border-white/5 pt-2">
                Delivers to Koramangala, Indiranagar, HSR
              </div>
            </div>

            <div className="group p-5 border border-white/10 hover:border-orange-500/50 bg-white/[0.02] transition-colors">
              <div className="flex items-center justify-between mb-3">
                <span className="text-sm font-bold uppercase tracking-wider text-white flex items-center gap-1.5">
                  <MapPin className="w-4 h-4 text-orange-500" /> Delhi NCR
                </span>
                <span className="text-[9px] px-1.5 py-0.5 bg-emerald-500/20 text-emerald-400 font-mono font-bold">ACTIVE</span>
              </div>
              <div className="text-xs text-white/40">Vendor: Artisan Bakes Connaught Place</div>
              <div className="mt-4 flex items-baseline justify-between">
                <div className="text-3xl font-black italic text-orange-500">₹729</div>
                <span className="text-[11px] font-mono text-white/40 uppercase">Lead: 3 hrs</span>
              </div>
              <div className="mt-3 text-[10px] font-mono text-white/30 border-t border-white/5 pt-2">
                Delivers to CP, South Delhi, Gurugram, Noida
              </div>
            </div>
          </div>
        </section>

        {/* REST API Explorer */}
        <section id="api-explorer" className="border border-white/10 bg-[#0E0E10] p-6 sm:p-8">
          <div className="flex flex-col sm:flex-row sm:items-end justify-between pb-6 border-b border-white/10 gap-4">
            <div>
              <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold block mb-1">Endpoint Catalog</span>
              <h3 className="text-3xl sm:text-4xl font-black tracking-tight uppercase text-white">API Registry</h3>
            </div>
            <div className="flex gap-2">
              <div className="px-3 py-1 bg-white/10 text-[10px] font-bold uppercase tracking-wider">REST</div>
              <div className="px-3 py-1 bg-white/10 text-[10px] font-bold uppercase tracking-wider">JSON</div>
              <div className="px-3 py-1 bg-orange-500/20 text-orange-400 text-[10px] font-bold uppercase tracking-wider">SPRING BOOT</div>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex flex-wrap gap-2 mt-6">
            {Object.keys(ENDPOINTS).map((group) => (
              <button
                key={group}
                onClick={() => setSelectedGroup(group)}
                className={`px-3.5 py-2 text-xs font-mono font-bold uppercase tracking-wider transition-colors border ${
                  selectedGroup === group
                    ? 'bg-orange-500 text-black border-orange-500 font-black'
                    : 'bg-white/[0.03] text-white/70 hover:bg-white/[0.08] border-white/10'
                }`}
              >
                {group}
              </button>
            ))}
          </div>

          {/* Endpoint List */}
          <div className="mt-6 space-y-4">
            {ENDPOINTS[selectedGroup].map((ep, idx) => (
              <div key={idx} className="group p-5 border border-white/5 hover:border-orange-500/50 bg-white/[0.02] transition-colors">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-2">
                  <div className="flex items-center space-x-2.5">
                    <span className={`px-2 py-0.5 text-xs font-mono font-bold border ${getMethodBadgeClass(ep.method)}`}>
                      {ep.method}
                    </span>
                    <span className="font-mono text-sm font-semibold text-orange-400">{ep.path}</span>
                  </div>
                  <div>
                    <span className={`px-2 py-0.5 text-[9px] font-mono font-bold uppercase border ${getRoleBadgeClass(ep.role)}`}>
                      {ep.role}
                    </span>
                  </div>
                </div>

                <p className="text-xs text-white/50">{ep.description}</p>

                {/* Request / Response samples */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mt-4">
                  {ep.bodySample && (
                    <div className="bg-[#0A0A0B] p-3 border border-white/10">
                      <div className="flex items-center justify-between mb-1.5">
                        <span className="text-[10px] font-mono uppercase tracking-widest text-orange-500 font-bold">Request Payload</span>
                        <button
                          onClick={() => copyToClipboard(ep.bodySample!, `req-${idx}`)}
                          className="text-white/40 hover:text-white text-xs flex items-center gap-1 font-mono"
                        >
                          {copiedKey === `req-${idx}` ? <Check className="w-3.5 h-3.5 text-green-400" /> : <Copy className="w-3.5 h-3.5" />}
                          <span className="text-[10px] uppercase">Copy</span>
                        </button>
                      </div>
                      <pre className="text-[11px] font-mono text-orange-200/90 overflow-x-auto p-2 bg-black/40">
                        {ep.bodySample}
                      </pre>
                    </div>
                  )}

                  {ep.responseSample && (
                    <div className="bg-[#0A0A0B] p-3 border border-white/10">
                      <div className="flex items-center justify-between mb-1.5">
                        <span className="text-[10px] font-mono uppercase tracking-widest text-green-400 font-bold">Response Sample (200 OK)</span>
                        <button
                          onClick={() => copyToClipboard(ep.responseSample!, `res-${idx}`)}
                          className="text-white/40 hover:text-white text-xs flex items-center gap-1 font-mono"
                        >
                          {copiedKey === `res-${idx}` ? <Check className="w-3.5 h-3.5 text-green-400" /> : <Copy className="w-3.5 h-3.5" />}
                          <span className="text-[10px] uppercase">Copy</span>
                        </button>
                      </div>
                      <pre className="text-[11px] font-mono text-green-300/90 overflow-x-auto p-2 bg-black/40">
                        {ep.responseSample}
                      </pre>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>

          {/* Bold Metrics Footer Bar from Design Theme */}
          <div className="mt-10 pt-6 border-t border-white/10 flex flex-wrap gap-8 sm:gap-12 items-center">
            <div>
              <div className="text-[10px] uppercase font-bold text-white/30 mb-1 tracking-widest">Entities</div>
              <div className="text-3xl font-black italic text-orange-500">12 <span className="text-white text-lg not-italic font-normal">MODELS</span></div>
            </div>
            <div>
              <div className="text-[10px] uppercase font-bold text-white/30 mb-1 tracking-widest">Status</div>
              <div className="text-3xl font-black italic text-orange-500">200 <span className="text-white text-lg not-italic font-normal">OK</span></div>
            </div>
            <div>
              <div className="text-[10px] uppercase font-bold text-white/30 mb-1 tracking-widest">Cloud Hub</div>
              <div className="text-3xl font-black italic text-orange-500 uppercase">Railway</div>
            </div>
            <div className="ml-auto">
              <div className="w-20 h-20 border border-white/20 rounded-full flex items-center justify-center relative">
                <div className="text-[10px] uppercase font-black tracking-tighter text-center leading-tight">
                  Deploy<br />Ready
                </div>
                <svg className="absolute inset-0 w-full h-full" viewBox="0 0 100 100">
                  <circle cx="50" cy="50" r="45" fill="none" stroke="#f97316" strokeWidth="1.5" strokeDasharray="10 5" />
                </svg>
              </div>
            </div>
          </div>
        </section>

        {/* Project Files & Railway Deployment Guide */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* File Architecture */}
          <div className="border border-white/10 bg-[#0E0E10] p-6 sm:p-8">
            <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold block mb-1">File Tree</span>
            <h3 className="text-2xl font-black tracking-tight uppercase text-white mb-2">
              Maven Project Artifacts
            </h3>
            <p className="text-xs text-white/40 mb-5">
              Source code organized in layered Java architecture under <code className="text-orange-400 font-mono">backend/</code>.
            </p>

            <div className="space-y-2 text-xs font-mono text-white/80">
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 backend/pom.xml</span>
                <span className="text-orange-400 text-[11px] font-bold">Java 17 / Spring Boot 3.2.4</span>
              </div>
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 backend/src/main/resources/application.yml</span>
                <span className="text-orange-400 text-[11px] font-bold">HikariCP & Supabase Pooler</span>
              </div>
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 backend/Dockerfile</span>
                <span className="text-orange-400 text-[11px] font-bold">Temurin JRE 17 Multi-stage</span>
              </div>
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 backend/railway.json & railway.toml</span>
                <span className="text-orange-400 text-[11px] font-bold">One-Click Railway Config</span>
              </div>
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 src/main/java/com/ibake/entity/* (10 Entities)</span>
                <span className="text-orange-400 text-[11px] font-bold">City Availability Triad</span>
              </div>
              <div className="p-3 bg-white/[0.02] border border-white/10 flex justify-between items-center">
                <span>📁 src/main/java/com/ibake/controller/* (7 Controllers)</span>
                <span className="text-orange-400 text-[11px] font-bold">@PreAuthorize Security</span>
              </div>
            </div>
          </div>

          {/* Railway Deployment Steps */}
          <div className="border border-white/10 bg-[#0E0E10] p-6 sm:p-8">
            <span className="text-[10px] uppercase tracking-[0.3em] text-orange-500 font-bold block mb-1">Environment</span>
            <h3 className="text-2xl font-black tracking-tight uppercase text-white mb-2">
              Railway & Supabase Config
            </h3>
            <p className="text-xs text-white/40 mb-5">
              Production environment variables defined in Railway deployment dashboard:
            </p>

            <div className="space-y-3">
              <div className="p-3.5 bg-white/[0.02] border border-white/10">
                <div className="flex items-center justify-between text-xs font-mono font-bold text-orange-400">
                  <span>DATABASE_URL</span>
                  <span className="text-[10px] text-white/40 font-normal">Supabase JDBC</span>
                </div>
                <p className="text-[11px] font-mono text-white/40 mt-1">
                  jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres?sslmode=require
                </p>
              </div>

              <div className="p-3.5 bg-white/[0.02] border border-white/10">
                <div className="flex items-center justify-between text-xs font-mono font-bold text-orange-400">
                  <span>JWT_SECRET</span>
                  <span className="text-[10px] text-white/40 font-normal">256-bit Key</span>
                </div>
                <p className="text-[11px] font-mono text-white/40 mt-1">
                  HS512 HMAC-SHA signing secret for stateless user authentication
                </p>
              </div>

              <div className="p-3.5 bg-white/[0.02] border border-white/10">
                <div className="flex items-center justify-between text-xs font-mono font-bold text-orange-400">
                  <span>CORS_ALLOWED_ORIGINS</span>
                  <span className="text-[10px] text-white/40 font-normal">React Frontend</span>
                </div>
                <p className="text-[11px] font-mono text-white/40 mt-1">
                  https://your-username.github.io,http://localhost:5173
                </p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer id="app-footer" className="h-14 border-t border-white/10 px-6 sm:px-10 flex flex-wrap items-center justify-between text-[10px] text-white/40 font-mono bg-[#0A0A0B]">
        <div>ENV: PROD-INDIAN-REGION-WEST-01</div>
        <div>&copy; 2024 IBAKE BACKEND INFRASTRUCTURE</div>
        <div>BUILD: v3.2.4-STABLE</div>
      </footer>
    </div>
  );
}
