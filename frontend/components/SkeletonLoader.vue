<template>
  <div class="skeleton-loader" aria-busy="true" aria-label="Loading content">
    <div v-for="i in count" :key="i" class="skeleton-item card p-4 sm:p-5 mb-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center flex-1">
          <div class="skeleton-checkbox mr-4 h-5 w-5 rounded bg-slate-200 animate-pulse"></div>
          <div class="skeleton-text h-5 bg-slate-200 rounded-md animate-pulse" :style="{ width: `${Math.floor(Math.random() * 50) + 30}%` }"></div>
        </div>
        <div class="flex space-x-3">
          <div class="skeleton-button h-8 w-8 rounded-md bg-slate-200 animate-pulse"></div>
          <div class="skeleton-button h-8 w-8 rounded-md bg-slate-200 animate-pulse"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: {
    type: Number,
    default: 3
  }
});
</script>

<style scoped>
.skeleton-item {
  animation: skeleton-fade 1.5s ease-in-out infinite;
}

@keyframes skeleton-fade {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}
</style>
