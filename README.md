部分页面
登录注册
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-02-27.jpg)
关键词检索，搜索结果可高亮显示
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-01-17.jpg)
具体论文详情，原件和附件来自用户，pdf和docx由服务器处理转换得到，调用python库实现
论文过长会调用单个模型，较短论文调用三个模型，2+1，2提取1评判，高亮最优结果
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-01-46.jpg)
知识图谱，使用Neovis.js渲染，重建图谱会先清除全部节点，重新创建，数据来自mysql
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-01-56.jpg)
论文上传
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-02-00.jpg)
嵌入neo4j，因为安全性无法直接使用iframe标签嵌入，采用Nginx反向代理neo4j，再将Nginx代理后的地址嵌入，接触限制
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-02-11.jpg)
用户论文信息管理，只能看到自己上传的论文
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-02-17.jpg)
管理员后台，公告、用户、论文管理
![图片文字描述](https://github.com/beomyo/literature_manager/blob/main/files/2025-03-13_11-02-37.jpg)
