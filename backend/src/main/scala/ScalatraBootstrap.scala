import javax.servlet.ServletContext
import com.nichoshop.servlets._
import com.nichoshop.servlets.admin.AdminAuthController
import com.nichoshop.servlets.swagger.{NichoSwagger, ResourcesApp}
import com.nichoshop.{Akka, WebApp, GlobalContext => GC}
import org.scalatra._
import org.slf4j.LoggerFactory

class ScalatraBootstrap extends LifeCycle {

  private val log = LoggerFactory.getLogger(classOf[ScalatraBootstrap])

  implicit val swagger = new NichoSwagger

  /** init Akka system */
  //  Akka

  override def init(context: ServletContext): Unit = {
    log.info("Context has been initialized. Initializing WebApp.....")
    GC.webApp = new WebApp
    log.info("WebApp has been initialized.")
    GC.applicationContext = context

    context.setInitParameter("org.scalatra.environment", "development")
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"

    context.mount(new InventoryController(), "/inventory/*", "inventory")
    context.mount(new CustomerCartController(), "/cart/*", "cart")
    context.mount(new PurchaseController, "/purchase/*", "purchase")
    context.mount(new SignUpController(GC.webApp.userService), "/signup/*", "signup")
    context.mount(new UsersController(GC.webApp.userService), "/user/*", "user")
    context.mount(new MessageController(GC.webApp.messageService), "/message/*", "message")
    context.mount(new CategoryController(GC.webApp.categoryService), "/category/*", "category")
    context.mount(new ProductController(GC.webApp.productService, GC.webApp.sellService), "/product/*", "product")
    context.mount(new SessionsController, "/sessions/*", "sessions")
    context.mount(new TwilioController, "/twilio/*", "twilio")
    context.mount(new SignInController(GC.webApp.userService), "/signin/*", "signin")

    context.mount(new seller.InventoryController(), "/seller/inventory/*", "seller/inventory")
    context.mount(new seller.AuctionController(), "/seller/auction/*", "seller/auction")
    context.mount(new AuctionController(), "/auction/*", "auction")

    // Admin
    context.mount(new AdminAuthController(), "/api/v1/admin/auth/*", "admin/auth")
    context.mount(new admin.UsersController(), "/admin/users/*", "admin/users")
    context.mount(new admin.CustomerSupportController(), "/admin/customer-supports/*", "admin/customer-supports")
    context.mount(new admin.CategoryController(), "/admin/category/*", "admin/category")

    context.mount(new ResourcesApp, "/api-docs")
  }

  override def destroy(context: ServletContext): Unit = {
    log.info("Context is about to be destroyed. Shutting down WebApp.")
    GC.webApp.shutdown()

    /** shutdown Akka system */
    //    Akka.system.shutdown()
  }
}
