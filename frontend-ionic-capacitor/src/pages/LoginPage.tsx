import { useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { AuthFormContainer } from '../components/auth/AuthFormContainer';
import { LoginForm } from '../components/auth/LoginForm';
import type { LoginRequest } from '../types/auth';

export function LoginPage() {
  const { login } = useAuth();
  const history = useHistory();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (data: LoginRequest) => {
    setError(null);
    setIsLoading(true);
    try {
      await login(data);
      history.replace('/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo iniciar sesión');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthFormContainer
      title="Iniciar sesión"
      subtitle="Ingresa a tu espacio de trabajo Innovatech"
      error={error}
      footer={<>¿No tienes cuenta? <Link to="/register">Crear cuenta</Link></>}
    >
      <LoginForm onSubmit={handleLogin} isLoading={isLoading} />
    </AuthFormContainer>
  );
}
