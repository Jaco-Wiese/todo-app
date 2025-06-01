/**
 * API Service for Todo App
 * Handles all API requests to the backend
 */

// Maximum number of retry attempts for failed requests
const MAX_RETRIES = 3;

// Base delay for exponential backoff (in milliseconds)
const BASE_RETRY_DELAY = 300;

// Base URL for API requests
const API_BASE_URL = process.client
  ? window.location.origin.replace(/:\d+$/, ':8080') // Default to port 8080 in browser
  : (process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8080');

/**
 * Makes an API request with retry capability
 * @param {string} url - The URL to fetch
 * @param {Object} options - Fetch options
 * @param {number} retries - Number of retries left
 * @returns {Promise<any>} - The response data
 */
async function fetchWithRetry(url, options = {}, retries = MAX_RETRIES) {
  try {
    const response = await fetch(url, options);

    // Check if the request was successful
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Request failed with status ${response.status}`);
    }

    // For DELETE requests or empty responses, return an empty object
    if (response.status === 204 || response.headers.get('content-length') === '0') {
      return {};
    }

    // Parse and return the response data
    return await response.json();
  } catch (error) {
    // If no more retries left, throw the error
    if (retries <= 0) {
      throw error;
    }

    // Calculate delay using exponential backoff
    const delay = BASE_RETRY_DELAY * Math.pow(2, MAX_RETRIES - retries);

    // Wait before retrying
    await new Promise(resolve => setTimeout(resolve, delay));

    // Retry the request
    return fetchWithRetry(url, options, retries - 1);
  }
}

/**
 * Formats error messages for user display
 * @param {Error} error - The error object
 * @param {string} fallbackMessage - Fallback message if error doesn't have a message
 * @returns {string} - Formatted error message
 */
function formatErrorMessage(error, fallbackMessage) {
  if (error.message && error.message.includes('Failed to fetch')) {
    return 'Network error. Please check your internet connection.';
  }

  return error.message || fallbackMessage;
}

/**
 * API Service object with methods for CRUD operations
 */
export const apiService = {
  /**
   * Fetches all todos from the API
   * @returns {Promise<Array>} - Array of todo items
   */
  async fetchTodos() {
    try {
      return await fetchWithRetry(`${API_BASE_URL}/todos`);
    } catch (error) {
      const errorMessage = formatErrorMessage(error, 'Failed to fetch todos');
      console.error('API Error:', error);
      throw new Error(errorMessage);
    }
  },

  /**
   * Creates a new todo
   * @param {Object} todo - The todo object to create
   * @returns {Promise<Object>} - The created todo
   */
  async createTodo(todo) {
    try {
      return await fetchWithRetry(`${API_BASE_URL}/todos`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(todo)
      });
    } catch (error) {
      const errorMessage = formatErrorMessage(error, 'Failed to create todo');
      console.error('API Error:', error);
      throw new Error(errorMessage);
    }
  },

  /**
   * Updates an existing todo
   * @param {string} id - The ID of the todo to update
   * @param {Object} todo - The updated todo data
   * @returns {Promise<Object>} - The updated todo
   */
  async updateTodo(id, todo) {
    try {
      return await fetchWithRetry(`${API_BASE_URL}/todos/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(todo)
      });
    } catch (error) {
      const errorMessage = formatErrorMessage(error, 'Failed to update todo');
      console.error('API Error:', error);
      throw new Error(errorMessage);
    }
  },

  /**
   * Deletes a todo
   * @param {string} id - The ID of the todo to delete
   * @returns {Promise<void>}
   */
  async deleteTodo(id) {
    try {
      await fetchWithRetry(`${API_BASE_URL}/todos/${id}`, {
        method: 'DELETE'
      });
    } catch (error) {
      const errorMessage = formatErrorMessage(error, 'Failed to delete todo');
      console.error('API Error:', error);
      throw new Error(errorMessage);
    }
  }
};
