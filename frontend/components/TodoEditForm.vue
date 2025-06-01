<template>
  <form @submit.prevent="submitForm" class="todo-edit-form card p-5">
    <div class="mb-5">
      <label for="edit-todo-title" class="block text-sm font-medium text-slate-700 mb-2">
        Edit Todo
      </label>
      <input
        id="edit-todo-title"
        v-model="editedTitle"
        type="text"
        placeholder="Update your todo"
        class="input"
        :class="{ 'border-red-500 focus:border-red-500 focus:ring-red-500': titleError }"
        @input="titleError = ''"
      />
      <p v-if="titleError" class="mt-2 text-sm text-red-600">{{ titleError }}</p>
    </div>

    <div class="mb-5">
      <label for="edit-todo-description" class="block text-sm font-medium text-slate-700 mb-2">
        Description (optional)
      </label>
      <textarea
        id="edit-todo-description"
        v-model="editedDescription"
        placeholder="Add details about this todo"
        class="input min-h-[80px]"
        rows="3"
      ></textarea>
    </div>

    <div class="flex justify-end space-x-3">
      <button
        type="button"
        @click="cancelEdit"
        class="btn-secondary"
      >
        Cancel
      </button>
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
        <span>{{ isSubmitting ? 'Saving...' : 'Save' }}</span>
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useTodoStore } from '~/stores/todo';

const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['cancel']);
const todoStore = useTodoStore();

const editedTitle = ref('');
const editedDescription = ref('');
const titleError = ref('');
const isSubmitting = ref(false);

// Initialize form with todo data
watch(() => props.todo, (newTodo) => {
  if (newTodo) {
    editedTitle.value = newTodo.title;
    editedDescription.value = newTodo.description || '';
  }
}, { immediate: true });

const submitForm = async () => {
  // Validate form
  if (!editedTitle.value.trim()) {
    titleError.value = 'Please enter a todo title';
    return;
  }

  try {
    isSubmitting.value = true;

    // Update todo
    await todoStore.updateTodo({
      ...props.todo,
      title: editedTitle.value.trim(),
      description: editedDescription.value.trim(),
      updatedAt: new Date().toISOString()
    });

    // Close edit form
    emit('cancel');

  } catch (error) {
    console.error('Error updating todo:', error);
    alert('Failed to update todo. Please try again.');
  } finally {
    isSubmitting.value = false;
  }
};

const cancelEdit = () => {
  emit('cancel');
};
</script>

<style scoped>
.todo-edit-form {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
