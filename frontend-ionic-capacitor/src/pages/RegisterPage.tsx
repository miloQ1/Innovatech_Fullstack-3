import { useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { AuthFormContainer } from '../components/auth/AuthFormContainer';
import { RegisterForm } from '../components/auth/RegisterForm';
import type { RegisterRequest } from '../types/auth';

export function RegisterPage() {
  const { register } = useAuth();
  const history = useHistory();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleRegister = async (data: RegisterRequest) => {
    setError(null);
    setIsLoading(true);
    try {
      await register(data);
      history.replace('/dashboard');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo crear la cuenta');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthFormContainer
      title="Crear cuenta"
      subtitle="Regístrate para colaborar en Innovatech"
      error={error}
      footer={<>¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link></>}
    >
      <RegisterForm onSubmit={handleRegister} isLoading={isLoading} />
    </AuthFormContainer>
  );
}
