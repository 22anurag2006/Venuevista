/* Venue Vista - Connected to Java Backend API */
(function () {
  const API_BASE_URL = 'http://localhost:8080/api';
  const HOLD_MS = 10 * 60 * 1000;

  let venuesCache = [];
  let bookingsCache = [];

  // Utility functions
  const iso = d => d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
  const day = n => { const d = new Date(); d.setDate(d.getDate() + n); return iso(d); };

  // API Call functions
  async function fetchVenues() {
    try {
      const response = await fetch(`${API_BASE_URL}/venues`);
      if (!response.ok) throw new Error('Failed to fetch venues');
      venuesCache = await response.json();
      return venuesCache;
    } catch (error) {
      console.error('Error fetching venues:', error);
      return [];
    }
  }

  async function fetchBookings() {
    try {
      const response = await fetch(`${API_BASE_URL}/bookings`);
      if (!response.ok) throw new Error('Failed to fetch bookings');
      bookingsCache = await response.json();
      return bookingsCache;
    } catch (error) {
      console.error('Error fetching bookings:', error);
      return [];
    }
  }

  async function fetchBookingsByUser(userId) {
    try {
      const response = await fetch(`${API_BASE_URL}/bookings/user/${userId}`);
      if (!response.ok) throw new Error('Failed to fetch user bookings');
      return await response.json();
    } catch (error) {
      console.error('Error fetching user bookings:', error);
      return [];
    }
  }

  async function createBookingAPI(bookingData) {
    try {
      const response = await fetch(`${API_BASE_URL}/bookings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(bookingData)
      });
      if (!response.ok) throw new Error('Failed to create booking');
      return await response.json();
    } catch (error) {
      console.error('Error creating booking:', error);
      return { error: error.message };
    }
  }

  // Functions that return data (kept for compatibility)
  function venue(id) {
    return venuesCache.find(v => v.id === Number(id));
  }

  function bookings() {
    return bookingsCache;
  }

  function get(id) {
    return bookingsCache.find(b => b.id === id);
  }

  // Date availability checking
  function blocked(venueId, date) {
    const n = Math.round((new Date(date + 'T00:00') - new Date(day(0) + 'T00:00')) / 86400000);
    return n >= 0 && (n + venueId * 2) % 6 === 0;
  }

  function taken(venueId, date) {
    return blocked(Number(venueId), date) || bookingsCache.some(b => b.venueId === Number(venueId) && b.date === date &&
      (b.status === 'confirmed' || (b.status === 'pending' && b.holdUntil > Date.now())));
  }

  function nextFree(venueId, count) {
    const out = [];
    for (let n = 1; n < 150 && out.length < count; n++) if (!taken(venueId, day(n))) out.push(day(n));
    return out;
  }

  async function create(venueId, date, guests) {
    const v = venue(venueId);
    if (!v) return { error: 'missing' };
    if (taken(v.id, date)) return { error: 'taken' };
    
    const bookingData = {
      venueId: v.id,
      date: date,
      guests: Number(guests),
      total: v.price,
      status: 'pending'
    };

    const result = await createBookingAPI(bookingData);
    if (result.error) return result;
    
    // Add to cache
    bookingsCache.push(result);
    return result;
  }

  async function update(id, patch) {
    const b = bookingsCache.find(x => x.id === id);
    if (b) {
      Object.assign(b, patch);
      // TODO: Implement PUT endpoint in backend to persist updates
    }
    return b;
  }

  // Initialize data on first load
  async function initialize() {
    await fetchVenues();
    await fetchBookings();
  }

  // Initialize when script loads
  initialize();

  // Expose public API
  window.VV = {
    venues: () => venuesCache,
    venue,
    bookings,
    getBookings: bookings,  // Alias for compatibility
    get,
    update,
    create,
    taken,
    nextFree,
    today: () => day(0),
    iso,
    fetchVenues,
    fetchBookings,
    fetchBookingsByUser,
    save: () => {} // No-op for localStorage compatibility
  };
})();