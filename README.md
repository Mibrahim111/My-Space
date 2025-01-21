# My-Space

MySpace is a social media platform that allows users to interact with each other through posts, comments, likes, and direct messages. The platform also supports group chats, communities, and user management. The project is built using Java and follows object-oriented design principles, including the use of design patterns such as Singleton, Factory, and Prototype.furtherMore , optimizing the application by using Data Structures provided by java collection like Hashmaps,Hashsets,ArrayLists,etc..and User Defined Data Strucutres like Trie.

Project Structure
The project is divided into several packages, each responsible for a specific functionality:

com.mySpace.chat: Handles chat functionality, including direct messages (DMs) and group chats.

com.mySpace.communities: Manages communities, including creating, viewing, and searching for communities.

com.mySpace.post: Manages posts, including creating, reposting, liking, and commenting on posts.

com.mySpace.user: Handles user-related operations, including registration, login, follow/unfollow, and user search.

com.utils.filesIO: Provides utility classes for file input/output operations, such as saving and loading data.

Key Classes and Their Responsibilities
Chat Functionality
Chat.java: Abstract class representing a chat. It contains common functionality for both direct messages and group chats, such as sending messages, managing participants, and displaying chat information.

Dm.java: Represents a direct message chat between two users. It extends the Chat class and provides functionality specific to DMs, such as starting a chat session and displaying messages.

GroupChat.java: Represents a group chat with multiple participants and admins. It extends the Chat class and provides functionality for managing group members and admins.

ChatController.java: Manages the creation, storage, and retrieval of chats. It also handles sending messages and saving/loading chat data to/from files.

Communities
Community.java: Represents a community with members, posts, and visibility settings. It provides functionality for adding/removing members and posts.

CommunitiesRepository.java: Manages the storage and retrieval of communities. It uses a singleton pattern to ensure a single instance of the repository.

CommunitySearchService.java: Provides search functionality for communities using a Trie data structure for efficient prefix-based searches.

CommunityServices.java: Provides services for creating, viewing, and listing communities.

Posts
Post.java: Represents a post made by a user. It includes functionality for likes, reposts, comments, and tagged users.

PostRepository.java: Manages the storage and retrieval of posts. It uses a singleton pattern to ensure a single instance of the repository.

PostService.java: Provides services for creating posts, reposting, liking, and commenting on posts.

PostFactory.java: Factory class for creating posts with different privacy strategies (public or private).

Users
User.java: Represents a user with attributes such as username, email, password, and followers. It provides functionality for managing followers, following, and restricted users.

UserRepository.java: Manages the storage and retrieval of users. It uses a singleton pattern to ensure a single instance of the repository.

UserService.java: Provides services for user registration, login, updating user information, and managing followers.

UserSearchService.java: Provides search functionality for users using a Trie data structure for efficient prefix-based searches.

Utilities
ArrayListIO.java: Utility class for reading and writing ArrayLists to/from files.

MapIO.java: Utility class for reading and writing Maps to/from files.

IFileHandler.java: Interface defining the contract for file handling operations.

Design Patterns Used
Singleton: Ensures that only one instance of a class is created. Used in UserRepository, PostRepository, and CommunitiesRepository.

Factory: Provides a way to create objects without specifying the exact class of the object. Used in PostFactory and RegularUserFactory.

Prototype: Allows objects to be copied or cloned. Used in User and Post classes for deep copying.

Observer: Used implicitly in the chat functionality where messages are sent to all participants in a chat.

Key Features
User Management:

User registration and login.

Follow/unfollow other users.

Block/unblock users.

Search for users by username or prefix.

Post Management:

Create, like, and repost posts.

Add comments and replies to posts.

Tag other users in posts.

Privacy settings for posts (public or private).

Chat Functionality:

Direct messages between two users.

Group chats with multiple participants and admins.

Send and receive messages in real-time.

Communities:

Create and join communities.

Add posts to communities.

Search for communities by name or prefix.

File Handling:

Save and load user, post, and chat data to/from files.

Ensure data persistence across application restarts.
