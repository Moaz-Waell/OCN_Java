document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".password-toggle").forEach((toggle) => {
    toggle.addEventListener("click", (e) => {
      const input = e.target.closest(".input-group").querySelector("input");
      const type = input.type === "password" ? "text" : "password";
      input.type = type;

      // Toggle icon classes
      e.target.classList.toggle("fa-eye");
      e.target.classList.toggle("fa-eye-slash");
    });
  });
});
