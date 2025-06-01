<template>
  <header class="bg-gradient-to-r from-sky-600 to-sky-700 text-white p-4 sm:p-5 shadow-md">
    <div class="container mx-auto flex flex-col sm:flex-row justify-between items-center">
      <h1 class="text-2xl sm:text-3xl font-bold mb-3 sm:mb-0 tracking-tight">
        <span class="inline-block transform hover:scale-105 transition-transform duration-200">TODO</span> App
      </h1>
      <div class="text-sm flex flex-wrap justify-center">
        <div class="px-3 py-1.5 bg-white bg-opacity-20 backdrop-blur-sm rounded-l-lg border-r border-white border-opacity-10">
          <span class="font-medium">{{ completedCount }}</span> completed
        </div>
        <div class="px-3 py-1.5 bg-white bg-opacity-30 backdrop-blur-sm rounded-r-lg">
          <span class="font-medium">{{ totalCount }}</span> total
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue';
import { useTodoStore } from '~/stores/todo';

const todoStore = useTodoStore();

const totalCount = computed(() => todoStore.todos.length);
const completedCount = computed(() => todoStore.todos.filter(todo => todo.completed).length);
</script>

<style scoped>
header {
  position: sticky;
  top: 0;
  z-index: 10;
}
</style>
