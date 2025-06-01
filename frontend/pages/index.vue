<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-b from-slate-50 to-slate-100">
    <Header />
    <main class="container mx-auto flex-grow p-4 sm:p-6 md:p-8">
      <TodoForm />
      <TodoList />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useTodoStore } from '~/stores/todo';

const todoStore = useTodoStore();

onMounted(async () => {
  await todoStore.fetchTodos();
});
</script>

<style scoped>
.min-h-screen {
  min-height: 100vh;
}
</style>
