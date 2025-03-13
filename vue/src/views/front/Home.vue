<template>
  <div class="home">
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-select
            v-model="searchForm.selectedField"
            placeholder="搜索字段"
            style="width: 120px;"
        >
          <el-option label="标题" value="title"></el-option>
          <el-option label="作者" value="author"></el-option>
          <el-option label="单位" value="organ"></el-option>
          <el-option label="来源库" value="srcDatabase"></el-option>
          <el-option label="文献来源" value="source"></el-option>
          <el-option label="关键词" value="keyword"></el-option>
          <el-option label="摘要" value="summary"></el-option>
          <el-option label="年份" value="year"></el-option>
          <el-option label="基金" value="fund"></el-option>
        </el-select>
        <el-input
            v-model="searchForm.inputValue"
            placeholder="请输入搜索内容"
            style="width: 300px;"
            @keyup.enter.native="handleSearch"
        ></el-input>
        <el-button type="primary" @click="handleSearch" style="margin-left:10px">搜索</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form>
    </div>

    <div class="result-section" v-if="articles.length > 0">
      <el-card
          v-for="article in articles"
          :key="article.id"
          class="article-card"
      >
        <div class="article-header" @click="viewDetail(article.title)">
          <h3 class="article-title" v-html="highlightText(article.title)"></h3>
        </div>
        <div class="article-info">
          <div class="info-row">
            <p><strong>作者：</strong><span v-html="highlightText(article.author)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>文献来源：</strong><span v-html="highlightText(article.source)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>发表时间：</strong><span v-html="highlightText(article.pubTime)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>单位：</strong><span v-html="highlightText(article.organ)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>关键词：</strong><span v-html="highlightText(article.keyword)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>分类号：</strong><span v-html="highlightText(article.clc)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>第一责任人：</strong><span v-html="highlightText(article.firstDuty)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>基金：</strong><span v-html="highlightText(article.fund)"></span></p>
          </div>
          <div class="info-row">
            <p><strong>页数：</strong>{{ article.pageCount }}</p>
          </div>
          <div class="info-row">
            <p v-if="article.url"><strong>URL：</strong><span v-html="highlightText(article.url)"></span></p>
          </div>
          <div class="info-row">
            <p v-if="article.doi"><strong>DOI：</strong><span v-html="highlightText(article.doi)"></span></p>
          </div>
          <div class="abstract-box">
            <strong>摘要：</strong>
            <div
                :class="['article-summary', article.isExpanded ? 'expanded' : 'collapsed']"
                v-html="highlightText(article.summary)"
            ></div>
            <el-button
                v-if="article.summary?.length > 100"
                type="text"
                @click="article.isExpanded = !article.isExpanded"
                class="toggle-btn"
            >
              {{ article.isExpanded ? '收起' : '展开' }}
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="prev, pager, next, sizes"
            :current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <div class="no-result" v-else-if="searched">
      <el-empty description="暂无搜索结果" :image-size="200"></el-empty>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Home',
  data() {
    return {
      searchForm: {
        selectedField: 'title',
        inputValue: ''
      },
      articles: [],
      searched: false,
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  mounted() {
    // 页面加载时自动执行搜索，默认获取所有数据
    this.handleSearch()
  },
  methods: {
    async handleSearch() {
      try {
        const searchParams = this.searchForm.inputValue
            ? { [this.searchForm.selectedField]: this.searchForm.inputValue }
            : {}; // 如果没有输入值，发送空对象以获取所有数据

        const res = await request.post('/article/search', searchParams, {
          params: {
            pageNum: this.pagination.pageNum,
            pageSize: this.pagination.pageSize
          }
        })
        this.articles = res.data.list.map(a => ({ ...a, isExpanded: false }))
        this.pagination.total = res.data.total
        this.searched = true
      } catch {
        this.$message.error('搜索失败')
      }
    },

    resetForm() {
      this.searchForm = { selectedField: 'title', inputValue: '' }
      this.articles = []
      this.searched = false
      this.pagination = {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
      // 重置后立即获取默认数据
      this.handleSearch()
    },

    viewDetail(title) {
      const route = this.$router.resolve({
        path: `/front/article/${title}`
      });
      window.open(route.href, '_blank');
    },

    highlightText(text) {
      if (!text || !this.searchForm.inputValue) return this.escapeHtml(text)
      const regex = new RegExp(this.escapeHtml(this.searchForm.inputValue), 'gi')
      return this.escapeHtml(text).replace(regex, match => `<mark>${match}</mark>`)
    },

    escapeHtml(text) {
      return String(text ?? '')
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
          .replace(/"/g, '&quot;')
          .replace(/'/g, '&#039;')
    },

    handleSizeChange(newSize) {
      this.pagination.pageSize = newSize
      this.pagination.pageNum = 1
      this.handleSearch()
    },

    handlePageChange(newPage) {
      this.pagination.pageNum = newPage
      this.handleSearch()
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.search-section {
  background: #fff;
  padding: 25px 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  margin-bottom: 30px;
}

.result-section {
  margin-top: 30px;
}

.article-card {
  margin-bottom: 25px;
  border-radius: 12px;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.article-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.12);
}

.article-header {
  cursor: pointer;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.article-title {
  color: #1a1a1a;
  margin: 0;
  font-size: 20px;
  transition: color 0.2s;
}

.article-title:hover {
  color: #1890ff;
}

.info-row {
  display: flex;
  gap: 30px;
  margin-bottom: 12px;
}

.info-row p {
  flex: 1;
  margin: 0;
  font-size: 14px;
  color: #4a4a4a;
}

.abstract-box {
  position: relative;
  background: #f8f9fa;
  border-radius: 6px;
  padding: 12px 15px;
  margin: 15px 0;
}

.article-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.article-summary.collapsed {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.toggle-btn {
  position: absolute;
  right: 15px;
  bottom: 5px;
  padding: 0 8px;
  background: rgba(255,255,255,0.9);
}

.no-result {
  margin: 50px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
</style>