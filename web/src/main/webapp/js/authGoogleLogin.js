window.onload = function () {
  google.accounts.id.initialize({
    client_id: "751231715559-3avnniuamcnc5a2dvcb88daonksn91p5.apps.googleusercontent.com",
    callback: onGoogleSignIn
  });

  // Botón Google del login
  const loginBtn = document.getElementById("googleLoginBtn");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      google.accounts.id.prompt();
    });
  }

  // Botón Google del registro
  const registerBtn = document.getElementById("googleRegisterBtn");
  if (registerBtn) {
    registerBtn.addEventListener("click", () => {
      google.accounts.id.prompt();
    });
  }
};


function onGoogleSignIn(response) {
  const idToken = response.credential;

  console.log("✅ ID Token recibido:", idToken);

  fetch('/auth/login-google', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ idToken: idToken })
  })
    .then(response => {
      if (!response.ok) throw new Error('Error al iniciar sesión con Google');
      return response.json();
    })
    .then(data => {
      console.log("✅ Login exitoso con Google:", data);
      mostrarToast("Login exitoso. ¡Bienvenido a Terra Nostra!", "success");
      setTimeout(() => window.location.reload(), 1500);
    })
    .catch(error => {
      console.error("❌ Error en login con Google:", error);
      mostrarToast("Error al iniciar sesión con Google. Inténtalo de nuevo.", "error");
    });
}

// Mostrar toast visual
function mostrarToast(mensaje, tipo) {
  const toastContainer = document.getElementById('toastContainer');

  const icon = tipo === "success"
    ? '<i class="bi bi-check-circle-fill text-success"></i>'
    : '<i class="bi bi-exclamation-triangle-fill text-danger"></i>';

  const bgClass = tipo === "success" ? "bg-success text-white" : "bg-danger text-white";

  const toastHTML = `
    <div class="toast ${bgClass}" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          ${icon} ${mensaje}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;

  toastContainer.insertAdjacentHTML('beforeend', toastHTML);

  const toastElement = toastContainer.lastElementChild;
  const bsToast = new bootstrap.Toast(toastElement);
  bsToast.show();

  toastElement.addEventListener('hidden.bs.toast', () => {
    toastElement.remove();
  });
}
