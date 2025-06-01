<template>
  <form @submit.prevent="submitForm" class="todo-form card p-5 mb-8">
    <div class="mb-5">
      <label for="todo-title" class="block text-sm font-medium text-slate-700 mb-2">
        New Todo
      </label>
      <input
        id="todo-title"
        v-model="title"
        type="text"
        placeholder="What needs to be done?"
        class="input"
        :class="{ 'border-red-500 focus:border-red-500 focus:ring-red-500': titleError }"
        @input="titleError = ''"
      />
      <p v-if="titleError" class="mt-2 text-sm text-red-600">{{ titleError }}</p>
    </div>

    <div class="mb-5">
      <label for="todo-description" class="block text-sm font-medium text-slate-700 mb-2">
        Description (optional)
      </label>
      <textarea
        id="todo-description"
        v-model="description"
        placeholder="Add details about this todo"
        class="input min-h-[80px]"
        rows="3"
      ></textarea>
    </div>

    <div class="flex justify-end">
      <button
        type="submit"
        class="btn-primary"
        :disabled="isSubmitting"
        :class="{ 'opacity-75 cursor-not-allowed': isSubmitting }"
        aria-live="polite"
      >
        <svg v-if="isSubmitting" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span>{{ isSubmitting ? 'Adding...' : 'Add Todo' }}</span>
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref } from 'vue';
import { useTodoStore } from '~/stores/todo';

const todoStore = useTodoStore();
const title = ref('');
const description = ref('');
const titleError = ref('');
const isSubmitting = ref(false);

const submitForm = async () => {
  // Validate form
  if (!title.value.trim()) {
    titleError.value = 'Please enter a todo title';
    return;
  }

  try {
    isSubmitting.value = true;

    // Create new todo
    await todoStore.createTodo({
      title: title.value.trim(),
      description: description.value.trim(),
      completed: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    });

    // Reset form
    title.value = '';
    description.value = '';

  } catch (error) {
    console.error('Error creating todo:', error);
    alert('Failed to create todo. Please try again.');
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.todo-form {
  transition: all 0.3s ease;
}
</style>
