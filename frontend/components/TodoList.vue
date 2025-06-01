<template>
  <div class="todo-list container mx-auto p-4" role="region" aria-label="Todo List">
    <div class="mb-6 flex flex-col sm:flex-row sm:justify-between sm:items-center space-y-4 sm:space-y-0 bg-white rounded-xl p-4 shadow-soft border border-slate-200">
      <div class="flex flex-wrap gap-2" role="group" aria-label="Sort options">
        <span class="text-sm text-slate-600 self-center mr-1 font-medium" id="sort-label">Sort by:</span>
        <button
          v-for="option in sortOptions"
          :key="option.value"
          @click="sortBy = option.value"
          :class="['px-3 py-1.5 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 transition-all duration-200',
                  sortBy === option.value ? 'bg-sky-600 text-white shadow-sm' : 'bg-slate-100 text-slate-700 hover:bg-slate-200']"
          :aria-pressed="sortBy === option.value"
          :aria-labelledby="'sort-label'"
          type="button"
        >
          {{ option.label }}
        </button>
      </div>
      <div class="flex flex-wrap gap-2" role="group" aria-label="Filter options">
        <span class="text-sm text-slate-600 self-center mr-1 font-medium" id="filter-label">Filter:</span>
        <button
          v-for="option in filterOptions"
          :key="option.value"
          @click="filterBy = option.value"
          :class="['px-3 py-1.5 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 transition-all duration-200',
                  filterBy === option.value ? 'bg-sky-600 text-white shadow-sm' : 'bg-slate-100 text-slate-700 hover:bg-slate-200']"
          :aria-pressed="filterBy === option.value"
          :aria-labelledby="'filter-label'"
          type="button"
        >
          {{ option.label }}
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <SkeletonLoader v-if="todoStore.loading" :count="3" />

    <!-- Empty state -->
    <div v-else-if="filteredAndSortedTodos.length === 0" class="card p-8 text-center text-slate-500 flex flex-col items-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mb-3 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
      </svg>
      <p class="text-lg">No todos found. Create one to get started!</p>
    </div>

    <!-- Todo list -->
    <div v-else role="list" aria-label="Todo items list" class="space-y-3">
      <TransitionGroup name="todo-list" tag="div" class="space-y-3">
        <template v-for="todo in filteredAndSortedTodos" :key="todo.id">
          <TodoEditForm
            v-if="editingTodo && editingTodo.id === todo.id"
            :todo="todo"
            @cancel="cancelEdit"
          />
          <TodoItem
            v-else
            :todo="todo"
            @edit="handleEdit"
          />
        </template>
      </TransitionGroup>
    </div>

    <!-- Error message -->
    <div v-if="todoStore.error" class="mt-4 p-4 bg-red-50 text-red-700 rounded-xl border border-red-200 shadow-sm">
      <p class="font-medium mb-1">Error</p>
      <p>{{ todoStore.error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useTodoStore } from '~/stores/todo';
import SkeletonLoader from './SkeletonLoader.vue';
import TodoEditForm from './TodoEditForm.vue';

const todoStore = useTodoStore();
const editingTodo = ref(null);

const handleEdit = (todo) => {
  editingTodo.value = todo;
};

const cancelEdit = () => {
  editingTodo.value = null;
};

// Sorting
const sortBy = ref('createdAt');
const sortOptions = [
  { label: 'Date Created', value: 'createdAt' },
  { label: 'Alphabetical', value: 'title' }
];

// Filtering
const filterBy = ref('all');
const filterOptions = [
  { label: 'All', value: 'all' },
  { label: 'Active', value: 'active' },
  { label: 'Completed', value: 'completed' }
];

const filteredTodos = computed(() => {
  if (filterBy.value === 'all') {
    return todoStore.todos;
  } else if (filterBy.value === 'active') {
    return todoStore.todos.filter(todo => !todo.completed);
  } else {
    return todoStore.todos.filter(todo => todo.completed);
  }
});

const filteredAndSortedTodos = computed(() => {
  return [...filteredTodos.value].sort((a, b) => {
    if (sortBy.value === 'createdAt') {
      return new Date(b.createdAt) - new Date(a.createdAt);
    } else {
      return a.title.localeCompare(b.title);
    }
  });
});
</script>

<style scoped>
/* Transition animations for todo items */
.todo-list-enter-active,
.todo-list-leave-active {
  transition: all 0.5s ease;
}

.todo-list-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.todo-list-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.todo-list-move {
  transition: transform 0.5s ease;
}
</style>
