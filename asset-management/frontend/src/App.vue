<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  width: 100%;
  height: 100vh;
}

.search-form .el-select,
.filter-form .el-select,
.toolbar .el-select {
  min-width: 168px;
}

.el-dialog .el-form-item .el-select {
  width: 100%;
}

@media (max-width: 768px) {
  .search-form .el-form-item,
  .filter-form .el-form-item {
    width: 100%;
  }

  .search-form .el-select,
  .filter-form .el-select,
  .search-form .el-input,
  .filter-form .el-input {
    width: 100%;
  }
}
</style>
