<template>
  <div
    class="todo-item card p-4 sm:p-5 flex flex-wrap sm:flex-nowrap items-center justify-between gap-3 group hover:shadow-hover max-w-full"
    role="listitem"
    :aria-label="`Todo item: ${todo.title}`"
  >
    <div class="flex items-center min-w-0 flex-1">
      <input
        type="checkbox"
        :checked="todo.completed"
        @change="toggleTodo"
        class="mr-4 h-5 w-5 flex-shrink-0 text-sky-600 rounded focus:ring-2 focus:ring-sky-500 cursor-pointer"
        :id="`todo-${todo.id}`"
        :aria-label="`Mark ${todo.title} as ${todo.completed ? 'incomplete' : 'complete'}`"
      >
      <div class="flex flex-col w-full overflow-hidden">
        <label
          :for="`todo-${todo.id}`"
          class="cursor-pointer text-slate-800 text-base overflow-hidden text-ellipsis break-words"
          :class="{ 'line-through text-slate-400': todo.completed }"
          :title="todo.title"
        >
          {{ todo.title }}
        </label>
        <p
          v-if="todo.description"
          class="text-sm text-slate-500 mt-1 overflow-hidden text-ellipsis break-words"
          :class="{ 'line-through text-slate-400': todo.completed }"
          :title="todo.description"
        >
          {{ todo.description }}
        </p>
      </div>
    </div>

    <div class="flex space-x-3 flex-shrink-0">
      <button
        @click="editTodo"
        class="p-1.5 text-slate-500 hover:text-sky-600 transition-colors focus:outline-none focus:ring-2 focus:ring-sky-500 rounded-md opacity-70 group-hover:opacity-100"
        :aria-label="`Edit todo: ${todo.title}`"
        type="button"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
      </button>
      <button
        @click="deleteTodo"
        class="p-1.5 text-slate-500 hover:text-red-600 transition-colors focus:outline-none focus:ring-2 focus:ring-red-500 rounded-md opacity-70 group-hover:opacity-100"
        :aria-label="`Delete todo: ${todo.title}`"
        type="button"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useTodoStore } from '~/stores/todo';

const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
});

const todoStore = useTodoStore();
const emit = defineEmits(['edit']);

const toggleTodo = () => {
  todoStore.updateTodo({
    ...props.todo,
    completed: !props.todo.completed
  });
};

const editTodo = () => {
  emit('edit', props.todo);
};

const deleteTodo = () => {
  if (confirm('Are you sure you want to delete this todo?')) {
    todoStore.deleteTodo(props.todo.id);
  }
};
</script>

<style scoped>
.todo-item {
  transition: all 0.2s ease;
}

.todo-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}
</style>
