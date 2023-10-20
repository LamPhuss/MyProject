# Forum project
A forum project using java-springboot framework, html, css, javascript and mysql
## Configuration
You can config all your needed for server, datasource in application.yml
### Sever configuration
![Server_config_image](image.png)
You can cofig your path, port in here
### Datasource configuration
![Spring_config_image](image-1.png)
You can config your application name, datasource which refer to your database server here(currently using my sql)
### Library configuration
![Library_config_image](image-2.png)
You need to load all maven repository here in order to use the application
### Database configuration
![Mysql_config_image](image-3.png)
You also need to create 3 table like this in MySQL to sync data
#### Users table
![user_table_image](image-4.png)
Users table will need to have corresponding fields
#### Posts table
![post_table_image](image-5.png)
Posts table will need to have corresponding fields
#### Comments table
![comments_table_image](image-6.png)
Comments table will need to have corresponding fields
## How to use
## For user
### Login  
![Login_image](image-7.png)
You can login into your account via this login page
### Register
![Register_image](image-8.png) 
Registering your new account (Those fields are required fields)
### Reset password
![reset_password](image-9.png)
If the reset process is successful, you will receive the following message
![reset_password_success](image-10.png)
### Forum
![Forums](image-11.png)
These are forum page with some features
![Common_features](image-12.png)
Here some common features
### Post
![Forums](image-11.png)
These are forum page with some features
![Common_features](image-12.png)
Here some common features
#### 1.Search post
![search_bar](image-21.png)
Using search bar to get which post that you want to see
![search_result](image-22.png)
Result will be like this
#### 2.View post
![post_detail](image-13.png)
This is what a post look like
##### a.Comment
![comment](image-14.png)
A new comment will be displayed at the end of the post
##### b.Reply
Reply feature is exactly the same with comment feature but it will display like this if you reply to someone
![reply](image-15.png)
#### 3.Create post
![create_post](image-16.png)
Enter the following two fields to create a new post
![create_post_success](image-17.png)
You will receive this message if you create post success
#### 4.Edit your post
![edit_post](image-18.png)
![edit_post_form](image-19.png)
You can choose 2 option (update your post or delete it)
#### 5.View your post
![view_user_post](image-20.png)
You can see all your posts here
### Profile
#### 1.View profile
![profile](image-23.png)
You and the others can see your profile like this
#### 2.Edit profile
You and admin can gain permission to edit your profile via "EDIT" button(this button will not be displayed if others see your profile)
![edit_profile](image-24.png)
This is editing form
![edit_form](image-27.png)
And this is what it looks like if you update your profile successfully
![edit_success](image-25.png)
## For Admin
The admin will be granted full privileges to edit posts, edit user information, and have an additional new function of user management
![management](image-28.png)
### Manage feature
![manage_view](image-29.png)
This page will display all the users in the database
![delete_success](image-30.png)
The message wil be returned if you delete an user success
![delete_you](image-31.png)
And if you delete your account, you will need to login again



