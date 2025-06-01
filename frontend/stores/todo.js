import { defineStore } from 'pinia';
import { apiService } from '~/utils/api';

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: [],
    loading: false,
    error: null,
    filter: 'all'
  }),

  getters: {
    // Get filtered todos based on the current filter
    filteredTodos: (state) => {
      if (state.filter === 'all') {
        return state.todos;
      } else if (state.filter === 'active') {
        return state.todos.filter(todo => !todo.completed);
      } else if (state.filter === 'completed') {
        return state.todos.filter(todo => todo.completed);
      }
      return state.todos;
    },

    // Get todos sorted by creation date (newest first)
    sortedByDate: (state) => {
      return [...state.todos].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    },

    // Get todos sorted alphabetically
    sortedAlphabetically: (state) => {
      return [...state.todos].sort((a, b) => a.title.localeCompare(b.title));
    },

    // Get the count of completed todos
    completedCount: (state) => {
      return state.todos.filter(todo => todo.completed).length;
    },

    // Get the total count of todos
    totalCount: (state) => {
      return state.todos.length;
    }
  },

  actions: {
    // Set the current filter
    setFilter(filter) {
      this.filter = filter;
    },

    // Fetch all todos from the API
    async fetchTodos() {
      this.loading = true;
      this.error = null;

      try {
        // Fetch todos from the API service
        this.todos = await apiService.fetchTodos();
      } catch (error) {
        console.error('Error fetching todos:', error);
        this.error = error.message || 'Failed to fetch todos. Please try again.';
      } finally {
        this.loading = false;
      }
    },

    // Create a new todo
    async createTodo(todo) {
      this.loading = true;
      this.error = null;

      try {
        // Create todo using the API service
        let newTodo;

        if (process.env.NODE_ENV === 'development') {
          try {
            // Try to use the API
            newTodo = await apiService.createTodo(todo);
          } catch (apiError) {
            console.warn('API unavailable, using mock implementation', apiError);
            // Fallback to mock implementation
            newTodo = {
              ...todo,
              id: Date.now().toString() // Generate a unique ID
            };
          }
        } else {
          // In production, always use the API
          newTodo = await apiService.createTodo(todo);
        }

        this.todos.push(newTodo);
        return newTodo;
      } catch (error) {
        console.error('Error creating todo:', error);
        this.error = error.message || 'Failed to create todo. Please try again.';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Update an existing todo
    async updateTodo(updatedTodo) {
      this.loading = true;
      this.error = null;

      try {
        // Ensure updatedAt is set
        const todoToUpdate = {
          ...updatedTodo,
          updatedAt: new Date().toISOString()
        };

        let result;

        if (process.env.NODE_ENV === 'development') {
          try {
            // Try to use the API
            result = await apiService.updateTodo(todoToUpdate.id, todoToUpdate);

            // Update the local state to match the API response
            const index = this.todos.findIndex(todo => todo.id === todoToUpdate.id);
            if (index !== -1) {
              this.todos[index] = result;
            }
          } catch (apiError) {
            console.warn('API unavailable, using mock implementation', apiError);
            // Fallback to mock implementation
            const index = this.todos.findIndex(todo => todo.id === todoToUpdate.id);
            if (index !== -1) {
              this.todos[index] = {
                ...this.todos[index],
                ...todoToUpdate
              };
              result = this.todos[index];
            } else {
              throw new Error('Todo not found');
            }
          }
        } else {
          // In production, always use the API
          result = await apiService.updateTodo(todoToUpdate.id, todoToUpdate);

          // Update the local state to match the API response
          const index = this.todos.findIndex(todo => todo.id === todoToUpdate.id);
          if (index !== -1) {
            this.todos[index] = result;
          }
        }

        return result;
      } catch (error) {
        console.error('Error updating todo:', error);
        this.error = error.message || 'Failed to update todo. Please try again.';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Delete a todo
    async deleteTodo(id) {
      this.loading = true;
      this.error = null;

      try {
        if (process.env.NODE_ENV === 'development') {
          try {
            // Try to use the API
            await apiService.deleteTodo(id);
          } catch (apiError) {
            console.warn('API unavailable, using mock implementation', apiError);
            // No additional action needed for mock implementation
          }
        } else {
          // In production, always use the API
          await apiService.deleteTodo(id);
        }

        // Remove the todo from the array
        this.todos = this.todos.filter(todo => todo.id !== id);
      } catch (error) {
        console.error('Error deleting todo:', error);
        this.error = error.message || 'Failed to delete todo. Please try again.';
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // Clear all completed todos
    async clearCompleted() {
      this.loading = true;
      this.error = null;

      try {
        const completedIds = this.todos.filter(todo => todo.completed).map(todo => todo.id);

        if (completedIds.length === 0) {
          return; // Nothing to delete
        }

        if (process.env.NODE_ENV === 'development') {
          try {
            // Try to use the API for each completed todo
            await Promise.all(completedIds.map(id => apiService.deleteTodo(id)));
          } catch (apiError) {
            console.warn('API unavailable, using mock implementation', apiError);
            // No additional action needed for mock implementation
          }
        } else {
          // In production, always use the API
          await Promise.all(completedIds.map(id => apiService.deleteTodo(id)));
        }

        // Remove completed todos from the array
        this.todos = this.todos.filter(todo => !todo.completed);
      } catch (error) {
        console.error('Error clearing completed todos:', error);
        this.error = error.message || 'Failed to clear completed todos. Please try again.';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});
