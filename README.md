
本地fork后同步remote端代码(就可以在你的github.com上得到最新的，要么重新fork,这个比较简单一点）
git remote add remote_utils https://github.com/air-project/project-utils.git
git fetch remote_utils
git merge remote_utils/master
git push origin master

