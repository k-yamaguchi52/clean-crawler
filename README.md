# Clean Crawler
## やること
指定したURL（シードURL）のページから同じドメインを持つページのリンクのみたどっていき、シードURLサイト内のページのHTMLをできるかぎり取得する。
## 使い方
1. [docker-compose.yml](docker-compose.yml)がある場所で`docker-compose up` を実行し、RabbitMQサーバを起動。
2. [application.properties](src/main/resources/application.properties)を設定。crawl_seed_urlにHTMLを取得したいサイトのシードURL、html_save_dirに取得したHTMLを保存するディレクトリのパスを指定する
3. [CrawlerApplication.main](src/main/java/com/example/cleancrawler/crawler/CrawlerApplication.java)を実行する。